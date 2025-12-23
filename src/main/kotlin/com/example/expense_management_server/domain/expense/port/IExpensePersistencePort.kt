package com.example.expense_management_server.domain.expense.port

import com.example.expense_management_server.domain.expense.model.ExpenseDomainModel
import java.util.UUID

interface IExpensePersistencePort {
    fun save(expenseData: ExpenseDomainModel): ExpenseDomainModel
    fun update(expenseId: UUID, expenseUpdatedData: ExpenseDomainModel): ExpenseDomainModel
    fun getById(expenseId: UUID): ExpenseDomainModel?
    fun getAllByBalanceGroup(balanceGroupId: UUID): List<ExpenseDomainModel>
    fun delete(expenseId: UUID)
}