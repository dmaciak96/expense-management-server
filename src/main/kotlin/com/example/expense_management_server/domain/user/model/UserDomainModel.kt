package com.example.expense_management_server.domain.user.model

import java.time.OffsetDateTime
import java.util.UUID

data class UserDomainModel(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val roles: List<UserRole>,
    val isEmailVerified: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val lastLoginAt: OffsetDateTime,
    val accountStatus: AccountStatus,
    val firstName: String,
    val lastName: String,
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