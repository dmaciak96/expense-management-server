package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document
data class AccountEntity(
    @Id val id: UUID,
    @CreatedDate val createdAt: Instant,
    @LastModifiedDate val lastUpdatedAt: Instant,
    @CreatedBy val createdBy: ApplicationUserEntity,
    val name: String,
    val currency: Currency,
    val members: Set<AccountMemberEntity>,
    val expenses: Set<ExpenseEntity>,
    val status: AccountStatus,
) {
    fun toDomain() = Account(
        id = this.id,
        createdAt = this.createdAt,
        lastUpdatedAt = this.lastUpdatedAt,
        createdBy = this.createdBy.toDomain(),
        name = this.name,
        currency = this.currency,
        members = this.members.map { it.toDomain() }.toSet(),
        expenses = this.expenses.map { it.toDomain() }.toSet(),
        status = this.status,
    )

    companion object {
        fun fromDomain(domain: Account) = AccountEntity(
            id = domain.id,
            createdAt = domain.createdAt,
            lastUpdatedAt = domain.lastUpdatedAt,
            createdBy = ApplicationUserEntity.fromDomain(domain.createdBy),
            name = domain.name,
            currency = domain.currency,
            members = domain.members
                .map { AccountMemberEntity.fromDomain(it) }
                .toSet(),
            expenses = domain.expenses
                .map { ExpenseEntity.fromDomain(it) }
                .toSet(),
            status = domain.status,
        )
    }
}
