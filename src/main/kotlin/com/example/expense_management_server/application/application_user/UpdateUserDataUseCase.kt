package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.adapter.validation.application_user.EmailAlreadyUsedValidator
import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import com.example.expense_management_server.domain.authentication.port.PasswordEncoderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class UpdateUserDataUseCase(
    userDataValidators: List<UserValidationPort>,
    private val userPersistencePort: UserPersistencePort,
    private val passwordEncoderPort: PasswordEncoderPort
) {
    private val userDataValidators = userDataValidators.filterNot { it is EmailAlreadyUsedValidator }

    fun execute(newUserData: ApplicationUser): ApplicationUser {
        LOGGER.info { "Updating user ${newUserData.email}" }
        val oldUser = userPersistencePort.findById(newUserData.id)
        val applicationUserToUpdate = oldUser.copy(
            firstName = newUserData.firstName,
            lastName = newUserData.lastName,
            password = newUserData.password,
            email = newUserData.email,
            phoneNumber = newUserData.phoneNumber,
            displayName = newUserData.displayName,
            avatarUrl = newUserData.avatarUrl,
            lastUpdatedAt = Instant.now()
        )
        checkIfAnotherUserUseTheSameEmail(
            updatingUserId = applicationUserToUpdate.id,
            email = applicationUserToUpdate.email
        )
        userDataValidators.forEach {
            LOGGER.debug { "Running ${it.javaClass.simpleName} to validate user data" }
            it.validate(applicationUserToUpdate)
        }
        LOGGER.info { "User ${applicationUserToUpdate.email} data are valid" }
        val encodedPassword = passwordEncoderPort.encode(applicationUserToUpdate.password)
        LOGGER.debug { "Password encoded successfully" }
        val user = userPersistencePort.update(applicationUserToUpdate.copy(password = encodedPassword))
        LOGGER.info { "User ${user.email} updated successfully" }
        return user
    }

    private fun checkIfAnotherUserUseTheSameEmail(updatingUserId: UUID, email: String) {
        try {
            val userWithSameEmail = userPersistencePort.findByEmail(email)
            if (userWithSameEmail.id != updatingUserId) {
                LOGGER.warn { "$email is used by different user. User won't be updated" }
                throw UserAlreadyExistsException("User with email $email already exists")
            }
        } catch (_: UserNotFoundException) {
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}