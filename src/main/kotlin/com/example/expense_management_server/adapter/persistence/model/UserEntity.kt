package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserRole
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Version
import java.time.OffsetDateTime
import java.util.UUID

@Entity
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    var email: String,
    var nickname: String?,
    var passwordHash: String,
    var isEmailVerified: Boolean,
    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime?,
    var lastLoginAt: OffsetDateTime?,

    @Enumerated(EnumType.STRING)
    var role: UserRole,

    @Enumerated(EnumType.STRING)
    var accountStatus: AccountStatus,

    @Version
    var version: Int? = null,

    @ManyToMany(mappedBy = "groupMembers")
    var balanceGroups: Set<BalanceGroupEntity> = emptySet(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}