package com.example.expense_management_server.domain.user.registration

import com.example.expense_management_server.domain.port.IEmailVerificationPort
import com.example.expense_management_server.domain.port.IPasswordEncoderPort
import com.example.expense_management_server.domain.port.IUserPersistencePort
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRegistrationDomainModel
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.domain.user.registration.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.registration.exception.UserAlreadyExistsException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.OffsetDateTime

@Service
class UserRegistrationService(
    private val userPersistencePort: IUserPersistencePort,
    private val passwordEncoderPort: IPasswordEncoderPort,
    private val emailVerificationPort: IEmailVerificationPort,
    private val passwordValidator: PasswordValidator,
    private val clock: Clock,
) : IUserRegistrationFacade {

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

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}