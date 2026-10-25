package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.adapter.validation.application_user.EmailAlreadyUsedValidator
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class UpdateUserDataUseCase(
    userDataValidators: List<UserValidationPort>,
    private val userPersistencePort: UserPersistencePort,
) {
    private val userDataValidators = userDataValidators.filterNot { it is EmailAlreadyUsedValidator }

    fun execute(newUserData: ApplicationUser): ApplicationUser {
        LOGGER.info { "Updating user ${newUserData.email}" }
        val oldUser = userPersistencePort.findById(newUserData.id)
        val applicationUserToUpdate = oldUser.copy(
            firstName = newUserData.firstName,
            lastName = newUserData.lastName,
            phoneNumber = newUserData.phoneNumber,
            displayName = newUserData.displayName,
            avatarUrl = newUserData.avatarUrl,
            lastUpdatedAt = Instant.now()
        )
        userDataValidators.forEach {
            LOGGER.debug { "Running ${it.javaClass.simpleName} to validate user data" }
            it.validate(applicationUserToUpdate)
        }
        LOGGER.info { "User ${applicationUserToUpdate.email} data are valid" }
        val user = userPersistencePort.update(applicationUserToUpdate)
        LOGGER.info { "User ${user.email} updated successfully" }
        return user
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}