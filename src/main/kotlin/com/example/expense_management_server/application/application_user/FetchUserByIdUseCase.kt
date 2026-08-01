package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class FetchUserByIdUseCase(
    private val userPersistencePort: UserPersistencePort
) {

    fun execute(applicationUserId: UUID): ApplicationUser {
        LOGGER.info { "Fetching user by ID $applicationUserId" }
        val applicationUser = userPersistencePort.findById(applicationUserId)
        LOGGER.info { "User $applicationUserId was found" }
        return applicationUser
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}