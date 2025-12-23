package com.example.expense_management_server.domain.facade

import com.example.expense_management_server.domain.expense.model.ExpenseDomainModel
import java.util.UUID

interface IExpenseManagementFacade {
    fun save(expenseDomainModel: ExpenseDomainModel): ExpenseDomainModel
    fun update(expenseId: UUID, expenseDomainModel: ExpenseDomainModel): ExpenseDomainModel
    fun delete(expenseId: UUID)
    fun getById(expenseId: UUID): ExpenseDomainModel
    fun getAllByBalanceGroup(balanceGroupId: UUID): List<ExpenseDomainModel>
}