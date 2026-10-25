package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import com.example.expense_management_server.domain.authentication.port.AuthenticationPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class UserAccountVerificationUseCase(
    private val authenticationPort: AuthenticationPort,
    private val userPersistencePort: UserPersistencePort,
) {
    fun execute(token: String) {
        val email = authenticationPort.validateVerificationTokenAndGetEmail(token)
        LOGGER.info { "$email verification successful" }

        LOGGER.info { "User activation" }
        val applicationUserToUpdate = userPersistencePort.findByEmail(email).copy(
            status = ApplicationUserStatus.ACTIVE,
        )
        val user = userPersistencePort.update(applicationUserToUpdate)
        LOGGER.info { "User ${user.email} has been activated" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}