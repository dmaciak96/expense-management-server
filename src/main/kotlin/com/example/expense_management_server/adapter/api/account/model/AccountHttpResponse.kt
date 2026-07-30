package com.example.expense_management_server.adapter.api.account.model

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
import java.time.Instant
import java.util.*

data class AccountHttpResponse(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val name: String,
    val currency: Currency,
    val status: AccountStatus,
) {
    companion object {
        fun fromDomain(account: Account) = AccountHttpResponse(
            id = account.id,
            createdAt = account.createdAt,
            lastUpdatedAt = account.lastUpdatedAt,
            name = account.name,
            currency = account.currency,
            status = account.status,
        )
    }
}
