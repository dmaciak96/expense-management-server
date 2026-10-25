package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.authentication.model.RefreshToken
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document
data class RefreshTokenEntity(
    @Id val id: String,
    @CreatedDate val createdAt: Instant,
    val expireAt: Instant,
    val revokedAt: Instant? = null,
    val applicationUserId: String,
    val token: String,
) {
    fun toDomain() = RefreshToken(
        id = UUID.fromString(id),
        createdAt = createdAt,
        expireAt = expireAt,
        applicationUserId = UUID.fromString(applicationUserId),
        token = UUID.fromString(token),
    )

    companion object {
        fun fromDomain(domain: RefreshToken) = RefreshTokenEntity(
            id = domain.id.toString(),
            createdAt = domain.createdAt,
            expireAt = domain.expireAt,
            applicationUserId = domain.applicationUserId.toString(),
            token = domain.token.toString(),
        )
    }
}