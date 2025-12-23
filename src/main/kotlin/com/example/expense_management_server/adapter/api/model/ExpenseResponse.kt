package com.example.expense_management_server.adapter.api.model

import com.example.expense_management_server.domain.expense.exception.ExpenseValidationException
import com.example.expense_management_server.domain.expense.model.ExpenseDomainModel
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
        fun from(expenseDomainModel: ExpenseDomainModel) = ExpenseResponse(
            id = expenseDomainModel.id ?: throw ExpenseValidationException("Id cannot be null"),
            name = expenseDomainModel.name,
            expenseOwnerId = expenseDomainModel.expenseOwnerId,
            amount = expenseDomainModel.amount,
            splitType = expenseDomainModel.splitType,
            createdAt = expenseDomainModel.createdAt,
            updatedAt = expenseDomainModel.updatedAt
        )
    }
}
