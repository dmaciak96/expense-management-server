package com.example.expense_management_server.domain.account.port

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountMember
import com.example.expense_management_server.domain.account.model.Expense
import java.util.*

interface AccountPersistencePort {
    fun create(account: Account): Account
    fun findByName(name: String): Account
    fun findById(id: UUID): Account
    fun findAllByCreatorId(userId: UUID): List<Account>
    fun update(account: Account): Account
    fun deleteById(id: UUID)

    fun addExpense(expense: Expense, accountId: UUID): Account
    fun removeExpense(accountId: UUID, expenseId: UUID): Account

    fun addAccountMember(accountId: UUID, accountMember: AccountMember): Account
    fun removeMember(accountId: UUID, userId: UUID): Account
}