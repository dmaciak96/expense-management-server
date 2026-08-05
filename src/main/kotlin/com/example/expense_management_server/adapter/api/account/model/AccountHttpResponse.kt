package com.example.expense_management_server.adapter.api.account.model

import com.example.expense_management_server.adapter.api.account_members.model.AccountMemberHttpResponse
import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserHttpResponse
import com.example.expense_management_server.adapter.api.expense.model.ExpenseHttpResponse
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
import java.time.Instant
import java.util.*

data class AccountHttpResponse(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val createdBy: ApplicationUserHttpResponse,
    val name: String,
    val currency: Currency,
    val status: AccountStatus,
    val members: List<AccountMemberHttpResponse>,
    val expenses: List<ExpenseHttpResponse>
) {
    companion object {
        fun fromDomain(account: Account) = AccountHttpResponse(
            id = account.id,
            createdAt = account.createdAt,
            lastUpdatedAt = account.lastUpdatedAt,
            name = account.name,
            currency = account.currency,
            status = account.status,
            createdBy = ApplicationUserHttpResponse.fromDomain(
                account.createdBy
            ),
            members = account.members.map { AccountMemberHttpResponse.fromDomain(it) }.toList(),
            expenses = account.expenses.map { ExpenseHttpResponse.fromDomain(it) }.toList()
        )
    }
}
