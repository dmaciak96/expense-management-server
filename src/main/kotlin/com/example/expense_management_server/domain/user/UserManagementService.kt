package com.example.expense_management_server.domain.user

import com.example.expense_management_server.domain.facade.IUserManagementFacade
import com.example.expense_management_server.domain.port.IEmailVerificationPort
import com.example.expense_management_server.domain.port.IPasswordEncoderPort
import com.example.expense_management_server.domain.port.IUserPersistencePort
import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRegistrationDomainModel
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

    override fun registerNewUser(userRegistrationModel: UserRegistrationDomainModel): UserDomainModel {
        LOGGER.info { "Registering new user account" }
        if (userPersistencePort.findUserAccountByEmail(userRegistrationModel.email) != null) {
            LOGGER.warn { "User account with specified e-mail address ${userRegistrationModel.email} already exists" }
            throw UserAlreadyExistsException(userRegistrationModel.email)
        }

        val passwordValidationErrors = passwordValidator.checkIfPasswordIsValid(userRegistrationModel.password)
        if (passwordValidationErrors.isNotEmpty()) {
            throw PasswordValidationException(passwordValidationErrors)
        }
        LOGGER.info { "Password validation finished" }

        val passwordHash = passwordEncoderPort.encodePassword(userRegistrationModel.password)
        LOGGER.debug { "Password encoded successfully" }

        val savedUserAccount = userPersistencePort.saveUserAccount(
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

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}