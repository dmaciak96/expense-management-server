package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserRole
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.OffsetDateTime
import java.util.UUID

@Entity
data class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
