package com.example.expense_management_server.domain.service

import com.example.expense_management_server.domain.expense.model.Expense
import java.util.UUID

interface ExpenseService {
    fun save(expense: Expense): Expense
    fun update(expenseId: UUID, expense: Expense): Expense
    fun delete(expenseId: UUID)
    fun getById(expenseId: UUID): Expense
    fun getAllByBalanceGroup(balanceGroupId: UUID): List<Expense>
}