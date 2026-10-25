package com.example.expense_management_server.application.authentication

import com.example.expense_management_server.domain.authentication.exception.RefreshTokenNotFoundException
import com.example.expense_management_server.domain.authentication.model.AuthenticationToken
import com.example.expense_management_server.domain.authentication.port.AuthenticationPort
import com.example.expense_management_server.domain.authentication.port.RefreshTokenPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class RefreshAccessTokenUseCase(
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val authenticationPort: AuthenticationPort
) {

    fun execute(refreshToken: String): AuthenticationToken {
        val token = refreshTokenPersistencePort.findByToken(refreshToken)
        val now = Instant.now()
        if (now.isAfter(token.expireAt)) {
            LOGGER.debug { "Refresh token expired at ${token.expireAt}" }
            throw RefreshTokenNotFoundException()
        }
        val accessToken = authenticationPort.generateAuthenticationToken(token)
        LOGGER.info { "Access token was refreshed by refresh token" }
        refreshTokenPersistencePort.delete(refreshToken)
        LOGGER.info { "Refresh token was invalidated" }
        return accessToken
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}