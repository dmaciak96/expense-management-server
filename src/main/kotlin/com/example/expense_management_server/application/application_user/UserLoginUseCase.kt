package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.authentication.model.AuthenticationToken
import com.example.expense_management_server.domain.authentication.model.UserAuthentication
import com.example.expense_management_server.domain.authentication.port.AuthenticationPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class UserLoginUseCase(
    private val authenticationPort: AuthenticationPort
) {
    fun execute(userAuthentication: UserAuthentication): AuthenticationToken {
        LOGGER.debug { "Executing user login: ${userAuthentication.email}" }
        val token = authenticationPort.generateAuthenticationToken(userAuthentication)
        LOGGER.debug { "Authentication token was generated successfully" }
        return token
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}