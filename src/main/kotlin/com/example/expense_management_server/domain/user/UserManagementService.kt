package com.example.expense_management_server.domain.user

import com.example.expense_management_server.domain.facade.IUserManagementFacade
import com.example.expense_management_server.domain.user.port.IEmailVerificationPort
import com.example.expense_management_server.domain.user.port.IPasswordEncoderPort
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import com.example.expense_management_server.domain.user.exception.NicknameValidationException
import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserHttpDomainModel
import com.example.expense_management_server.domain.user.model.UserRole
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

@Service
class UserManagementService(
    private val userPersistencePort: IUserPersistencePort,
    private val passwordEncoderPort: IPasswordEncoderPort,
    private val emailVerificationPort: IEmailVerificationPort,
    private val passwordValidator: PasswordValidator,
    private val clock: Clock,
) : IUserManagementFacade {

    override fun registerNewUser(userRegistrationModel: UserHttpDomainModel): UserDomainModel {
        LOGGER.info { "Registering new user account" }
        checkIfEmailAlreadyExists(userRegistrationModel.email)

        validatePassword(userRegistrationModel.password)
        LOGGER.info { "Password validation finished" }

        val passwordHash = passwordEncoderPort.encodePassword(userRegistrationModel.password)
        LOGGER.debug { "Password encoded successfully" }

        checkIfNicknameIsEmpty(userRegistrationModel.nickname)
        val savedUserAccount = userPersistencePort.saveOrUpdateUserAccount(
            UserDomainModel(
                id = null,
                email = userRegistrationModel.email,
                nickname = userRegistrationModel.nickname,
                passwordHash = passwordHash,
                role = UserRole.USER,
                isEmailVerified = false,
                createdAt = OffsetDateTime.now(clock),
                updatedAt = null,
                lastLoginAt = null,
                accountStatus = AccountStatus.ACTIVE
            )
        )
        LOGGER.info { "New user account registered" }

        LOGGER.info { "Sending verification e-mail to ${savedUserAccount.email}" }
        emailVerificationPort.sendEmailVerificationMessage(savedUserAccount.email)
        LOGGER.info { "Verification e-mail was sent to ${savedUserAccount.email}" }

        return savedUserAccount
    }

    override fun getUserById(id: UUID): UserDomainModel {
        LOGGER.info { "Fetching registered user by id" }
        val user = userPersistencePort.findUserAccountById(id) ?: throw UserNotFoundException()
        return user
    }

    override fun getUserByEmail(email: String): UserDomainModel {
        LOGGER.info { "Fetching registered user by email: $email" }
        val user = userPersistencePort.findUserAccountByEmail(email) ?: throw UserNotFoundException()
        return user
    }

    override fun updateUser(
        userId: UUID,
        userUpdateModel: UserHttpDomainModel
    ): UserDomainModel {
        LOGGER.info { "Updating user account $userId" }
        val targetUserAccount = userPersistencePort.findUserAccountById(userId) ?: throw UserNotFoundException()
        if (userUpdateModel.email != targetUserAccount.email) {
            LOGGER.info { "Checking if account with specified email ${userUpdateModel.email} already exists" }
            checkIfEmailAlreadyExists(userUpdateModel.email)
        }
        validatePassword(userUpdateModel.password)
        LOGGER.info { "Password validation finished" }

        val passwordHash = passwordEncoderPort.encodePassword(userUpdateModel.password)
        LOGGER.debug { "Password encoded successfully" }

        checkIfNicknameIsEmpty(userUpdateModel.nickname)

        val email = if (targetUserAccount.email == userUpdateModel.email) {
            targetUserAccount.email
        } else {
            userUpdateModel.email
        }

        val nickname = if (targetUserAccount.nickname == userUpdateModel.nickname) {
            targetUserAccount.nickname
        } else {
            userUpdateModel.nickname
        }

        val isEmailVerified = if (targetUserAccount.email == userUpdateModel.email) {
            targetUserAccount.isEmailVerified
        } else {
            false
        }

        val savedUserAccount = userPersistencePort.saveOrUpdateUserAccount(
            UserDomainModel(
                id = userId,
                email = email,
                nickname = nickname,
                passwordHash = passwordHash,
                role = targetUserAccount.role,
                isEmailVerified = isEmailVerified,
                createdAt = targetUserAccount.createdAt,
                updatedAt = OffsetDateTime.now(clock),
                lastLoginAt = targetUserAccount.lastLoginAt,
                accountStatus = targetUserAccount.accountStatus
            )
        )
        LOGGER.info { "User account $userId was updated successfully" }

        if (targetUserAccount.email != userUpdateModel.email) {
            LOGGER.info { "Sending verification e-mail to ${savedUserAccount.email}" }
            emailVerificationPort.sendEmailVerificationMessage(savedUserAccount.email)
            LOGGER.info { "Verification e-mail was sent to ${savedUserAccount.email}" }
        }

        return savedUserAccount
    }

    override fun deleteUser(userId: UUID) {
        LOGGER.info { "Removing user from system" }
        val userToRemove = userPersistencePort.findUserAccountById(userId) ?: throw UserNotFoundException()
        userPersistencePort.deleteUser(userToRemove)
        LOGGER.info { "User ${userToRemove.email} was successfully removed" }
    }

    private fun checkIfEmailAlreadyExists(email: String) {
        if (userPersistencePort.findUserAccountByEmail(email) != null) {
            LOGGER.warn { "User account with specified e-mail address $email already exists" }
            throw UserAlreadyExistsException(email)
        }
    }

    private fun checkIfNicknameIsEmpty(nickname: String?) {
        if (nickname == null) {
            return
        }
        if (nickname.trim().isEmpty()) {
            throw NicknameValidationException("Nickname is empty after removing whitespaces")
        }
    }

    private fun validatePassword(password: String) {
        val passwordValidationErrors = passwordValidator.validate(password)
        if (passwordValidationErrors.isNotEmpty()) {
            throw PasswordValidationException(passwordValidationErrors)
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}