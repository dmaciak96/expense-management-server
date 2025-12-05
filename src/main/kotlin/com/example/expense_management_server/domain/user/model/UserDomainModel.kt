package com.example.expense_management_server.domain.user.model

import java.time.OffsetDateTime
import java.util.UUID

data class UserDomainModel(
    val id: UUID?,
    val email: String,
    val nickname: String?,
    val passwordHash: String,
    val role: UserRole,
    val isEmailVerified: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
    val lastLoginAt: OffsetDateTime?,
    val accountStatus: AccountStatus,
)

data class UserHttpDomainModel(
    val email: String,
    val password: String,
    val nickname: String?
)

enum class UserRole {
    USER,
    ADMIN
}

enum class AccountStatus {
    ACTIVE,
    INACTIVE,
    BLOCKED
}