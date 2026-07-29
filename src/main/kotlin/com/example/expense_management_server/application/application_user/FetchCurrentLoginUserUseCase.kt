package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.exception.UserNotLoggedInException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class FetchCurrentLoginUserUseCase(
    private val userPersistencePort: UserPersistencePort
) {
    fun execute(): ApplicationUser {
        LOGGER.info { "Fetching current login in user" }
        val authentication = SecurityContextHolder.getContext().authentication
        val currentUserEmail = authentication?.name ?: throw UserNotLoggedInException()
        LOGGER.info { "Current login user is $currentUserEmail" }
        return userPersistencePort.findByEmail(currentUserEmail)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}