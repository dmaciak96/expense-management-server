package com.example.expense_management_server.adapter.persistence.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@EntityListeners(AuditingEntityListener::class)
class BalanceGroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @Version
    var version: Int? = null,

    @CreatedBy
    var createdById: UUID? = null,

    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime?,
    var groupName: String,

    @ManyToMany
    @JoinTable(
        name = "balance_group_member",
        joinColumns = [JoinColumn(name = "balance_group_id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "user_id", nullable = false)]
    )
    var groupMembers: Set<UserEntity> = emptySet(),

    @OneToMany(
        mappedBy = "balanceGroup",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var expenses: List<ExpenseEntity> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BalanceGroupEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}