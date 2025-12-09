package com.example.expense_management_server.adapter.api.model

import com.example.expense_management_server.domain.user.model.AccountStatus
import java.time.OffsetDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID?,
    val email: String,
    val nickname: String?,
    val isEmailVerified: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
    val lastLoginAt: OffsetDateTime?,
    val accountStatus: AccountStatus,
)
