package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserRole
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Version
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

    @Enumerated(EnumType.STRING)
    val role: UserRole,
    val isEmailVerified: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
    val lastLoginAt: OffsetDateTime?,

    @Enumerated(EnumType.STRING)
    val accountStatus: AccountStatus,

    @Version
    val version: Int? = null,
)
