package com.example.expense_management_server.adapter.api.model

import com.example.expense_management_server.domain.expense.exception.ExpenseValidationException
import com.example.expense_management_server.domain.expense.model.Expense
import com.example.expense_management_server.domain.expense.model.ExpenseSplitType
import java.time.OffsetDateTime
import java.util.UUID

data class ExpenseResponse(
    val id: UUID,
    val name: String,
    val expenseOwnerId: UUID,
    val amount: Double,
    val splitType: ExpenseSplitType,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?
) {
    companion object {
        fun from(expense: Expense) = ExpenseResponse(
            id = expense.id ?: throw ExpenseValidationException("Id cannot be null"),
            name = expense.name,
            expenseOwnerId = expense.expenseOwnerId,
            amount = expense.amount,
            splitType = expense.splitType,
            createdAt = expense.createdAt,
            updatedAt = expense.updatedAt
        )
    }
}
