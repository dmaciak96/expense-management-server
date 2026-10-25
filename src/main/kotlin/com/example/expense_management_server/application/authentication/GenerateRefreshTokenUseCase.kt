package com.example.expense_management_server.application.authentication

import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.application.application_user.FetchUserByIdUseCase
import com.example.expense_management_server.domain.authentication.model.RefreshToken
import com.example.expense_management_server.domain.authentication.port.RefreshTokenPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@Component
class GenerateRefreshTokenUseCase(
    private val refreshTokenPersistencePort: RefreshTokenPersistencePort,
    private val fetchUserByEmailUseCase: FetchUserByEmailUseCase,
    private val fetchUserByIdUseCase: FetchUserByIdUseCase,
) {
    fun execute(email: String): RefreshToken {
        val user = fetchUserByEmailUseCase.execute(email)
        refreshTokenPersistencePort.deleteAllByUserId(user.id)
        LOGGER.info { "Generating new refresh token" }
        val now = Instant.now()
        val token = refreshTokenPersistencePort.create(
            RefreshToken(
                id = UUID.randomUUID(),
                createdAt = now,
                expireAt = OffsetDateTime.now()
                    .plusHours(1)
                    .toInstant(),
                applicationUserId = user.id,
                token = UUID.randomUUID()
            )
        )
        LOGGER.debug { "Generated new refresh token for user $email" }
        return token
    }

    fun execute(applicationUserId: UUID): RefreshToken {
        val user = fetchUserByIdUseCase.execute(applicationUserId)
        LOGGER.info { "Generating new refresh token" }
        val now = Instant.now()
        val token = refreshTokenPersistencePort.create(
            RefreshToken(
                id = UUID.randomUUID(),
                createdAt = now,
                expireAt = OffsetDateTime.now()
                    .plusHours(1)
                    .toInstant(),
                applicationUserId = user.id,
                token = UUID.randomUUID()
            )
        )
        LOGGER.debug { "Generated new refresh token for user $applicationUserId" }
        return token
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}