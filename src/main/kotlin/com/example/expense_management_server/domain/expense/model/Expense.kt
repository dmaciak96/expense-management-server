package com.example.expense_management_server.domain.expense.model

import java.time.OffsetDateTime
import java.util.UUID

data class Expense(
    val id: UUID?,
    val name: String,
    val balanceGroupId: UUID,
    val expenseOwnerId: UUID,
    val amount: Double,
    val splitType: ExpenseSplitType,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?
)
