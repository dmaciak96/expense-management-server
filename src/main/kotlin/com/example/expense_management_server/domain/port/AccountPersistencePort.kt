package com.example.expense_management_server.domain.port

import com.example.expense_management_server.domain.model.Account
import com.example.expense_management_server.domain.model.Expense
import java.util.*

interface AccountPersistencePort {
    fun create(user: Account): Account
    fun findByName(name: String): Account
    fun findById(id: UUID): Account
    fun update(account: Account): Account
    fun deleteById(id: UUID)

    fun addExpense(expense: Expense, accountId: UUID): Account
    fun removeExpense(accountId: UUID, expenseId: UUID): Account

    fun addAccountMember(accountId: UUID, userId: UUID): Account
    fun removeMember(accountId: UUID, userId: UUID): Account
}