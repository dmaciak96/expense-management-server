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
class ExpenseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,

    @Version
    var version: Int? = null,

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    var createdBy: UserEntity? = null,

    var createdAt: OffsetDateTime,
    var updatedAt: OffsetDateTime?,
    var name: String,
    var amount: Double,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "balance_group_id", nullable = false)
    var balanceGroup: BalanceGroupEntity,

    @Enumerated(EnumType.STRING)
    var splitType: ExpenseSplitType,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExpenseEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
