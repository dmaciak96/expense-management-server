package com.example.expense_management_server.adapter.persistence.config

import com.example.expense_management_server.adapter.persistence.model.ApplicationUserEntity
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.AuditorAware
import java.util.*


class CurrentLoggedUserAuditorAware(
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase
) : AuditorAware<ApplicationUserEntity> {
    override fun getCurrentAuditor(): Optional<ApplicationUserEntity> {
        try {
            LOGGER.debug { "Running user auditor aware" }
            val currentUser = ApplicationUserEntity.fromDomain(fetchCurrentLoginUserUseCase.execute())
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