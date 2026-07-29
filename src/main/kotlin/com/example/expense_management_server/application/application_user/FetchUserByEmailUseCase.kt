package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class FetchUserByEmailUseCase(
    private val userPersistencePort: UserPersistencePort
) {

    fun execute(email: String): ApplicationUser {
        LOGGER.info { "Fetching user by email: $email" }
        val user = userPersistencePort.findByEmail(email)
        if (user.status != ApplicationUserStatus.ACTIVE) {
            throw UserNotFoundException("User $email not found")
        }
        LOGGER.info { "User $email was found" }
        return user
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}