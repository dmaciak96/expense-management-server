package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.account.model.Expense
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document
data class ExpenseEntity(
    @Id val id: String,
    @CreatedDate val createdAt: Instant,
    @LastModifiedDate val lastUpdatedAt: Instant,
    @CreatedBy val createdBy: ApplicationUserEntity,
    val paidBy: ApplicationUserEntity,
    val name: String,
    val monetaryAmount: Long
) {
    fun toDomain() = Expense(
        id = UUID.fromString(this.id),
        createdAt = this.createdAt,
        lastUpdatedAt = this.lastUpdatedAt,
        createdBy = this.createdBy.toDomain(),
        paidBy = this.paidBy.toDomain(),
        name = this.name,
        monetaryAmount = this.monetaryAmount
    )

    companion object {
        fun fromDomain(domain: Expense) = ExpenseEntity(
            id = domain.id.toString(),
            createdAt = domain.createdAt,
            lastUpdatedAt = domain.lastUpdatedAt,
            createdBy = ApplicationUserEntity.fromDomain(domain.createdBy),
            paidBy = ApplicationUserEntity.fromDomain(domain.paidBy),
            name = domain.name,
            monetaryAmount = domain.monetaryAmount
        )
    }
}
