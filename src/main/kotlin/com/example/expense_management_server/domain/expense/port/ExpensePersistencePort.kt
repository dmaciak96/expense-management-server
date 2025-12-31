package com.example.expense_management_server.domain.expense.port

import com.example.expense_management_server.domain.expense.model.Expense
import java.util.UUID

interface ExpensePersistencePort {
    fun save(expense: Expense): Expense
    fun update(expenseId: UUID, expense: Expense): Expense
    fun getById(expenseId: UUID): Expense?
    fun getAllByBalanceGroup(balanceGroupId: UUID): List<Expense>
    fun delete(expenseId: UUID)
}