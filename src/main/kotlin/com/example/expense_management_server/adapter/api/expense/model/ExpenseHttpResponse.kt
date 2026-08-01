package com.example.expense_management_server.adapter.api.expense.model

import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserHttpResponse
import com.example.expense_management_server.domain.account.model.Expense
import java.time.Instant
import java.util.*

data class ExpenseHttpResponse(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val createdBy: ApplicationUserHttpResponse,
    val paidBy: ApplicationUserHttpResponse,
    val name: String,
    val monetaryAmount: Long
) {
    companion object {
        fun fromDomain(expense: Expense) = ExpenseHttpResponse(
            id = expense.id,
            createdAt = expense.createdAt,
            lastUpdatedAt = expense.lastUpdatedAt,
            createdBy = ApplicationUserHttpResponse.fromDomain(expense.createdBy),
            paidBy = ApplicationUserHttpResponse.fromDomain(expense.paidBy),
            name = expense.name,
            monetaryAmount = expense.monetaryAmount
        )
    }
}
