package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.expense.model.ExpenseSplitType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Version
import org.springframework.data.annotation.CreatedBy
import java.time.OffsetDateTime
import java.util.UUID

@Entity
data class ExpenseEntity(
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
    val name: String,
    val amount: Double,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "balance_group_id", nullable = false)
    val balanceGroup: BalanceGroupEntity,

    @Enumerated(EnumType.STRING)
    val splitType: ExpenseSplitType,
)
