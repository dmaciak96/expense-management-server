package com.example.expense_management_server.adapter.persistence.config

import com.example.expense_management_server.adapter.persistence.model.ApplicationUserEntity
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*


class CurrentLoggedUserAuditorAware(
    private val userPersistencePort: UserPersistencePort
) : AuditorAware<ApplicationUserEntity> {
    override fun getCurrentAuditor(): Optional<ApplicationUserEntity> {
        try {
            val authentication = SecurityContextHolder.getContext().authentication
            val currentUserEmail = authentication?.name ?: return Optional.empty()
            val currentUser = ApplicationUserEntity.fromDomain(userPersistencePort.findByEmail(currentUserEmail))
            return Optional.of(currentUser)
        } catch (ex: Exception) {
            LOGGER.warn { "User is not logged in. Returning empty object (cause: ${ex.message})" }
            return Optional.empty()
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}