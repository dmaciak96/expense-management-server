package com.example.expense_management_server.adapter.persistence.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedBy
import java.time.OffsetDateTime
import java.util.UUID

@Entity
data class BalanceGroupEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,

    @Version
    val version: Int? = null,

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    val createdBy: UserEntity? = null,

    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?,
    val groupName: String,

    @ManyToMany
    @JoinTable(
        name = "balance_group_member",
        joinColumns = [JoinColumn(name = "balance_group_id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "user_id", nullable = false)]
    )
    val groupMembers: Set<UserEntity> = emptySet(),

    @OneToMany(
        mappedBy = "balanceGroup",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val expenses: List<ExpenseEntity> = emptyList(),
)