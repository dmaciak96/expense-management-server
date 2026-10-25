package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.RefreshTokenEntity
import com.example.expense_management_server.adapter.persistence.repository.RefreshTokenRepository
import com.example.expense_management_server.domain.authentication.exception.RefreshTokenNotFoundException
import com.example.expense_management_server.domain.authentication.model.RefreshToken
import com.example.expense_management_server.domain.authentication.port.RefreshTokenPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository
) : RefreshTokenPersistencePort {
    override fun create(refreshToken: RefreshToken): RefreshToken {
        LOGGER.debug { "Saving new refresh token in DB (userId = ${refreshToken.applicationUserId})" }
        val savedEntity = refreshTokenRepository.save(RefreshTokenEntity.fromDomain(refreshToken))
        val savedDomain = savedEntity.toDomain()
        LOGGER.debug { "Refresh token was saved for user ${savedDomain.applicationUserId}" }
        return savedDomain
    }

    override fun findByToken(token: String): RefreshToken {
        val tokenEntity =
            refreshTokenRepository.findByToken(token) ?: throw RefreshTokenNotFoundException()
        val domain = tokenEntity.toDomain()
        return domain
    }

    override fun delete(token: String) {
        val tokenEntity =
            refreshTokenRepository.findByToken(token) ?: throw RefreshTokenNotFoundException()
        refreshTokenRepository.delete(tokenEntity)
    }

    override fun deleteAllByUserId(userId: UUID) {
        val tokens = refreshTokenRepository.findAllByApplicationUserId(userId.toString())
        refreshTokenRepository.deleteAll(tokens)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}