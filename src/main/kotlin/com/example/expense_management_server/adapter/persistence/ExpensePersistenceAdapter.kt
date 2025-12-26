package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.ExpenseEntity
import com.example.expense_management_server.adapter.persistence.repository.BalanceGroupRepository
import com.example.expense_management_server.adapter.persistence.repository.ExpenseRepository
import com.example.expense_management_server.domain.balancegroup.exception.BalanceGroupNotFoundException
import com.example.expense_management_server.domain.expense.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.expense.exception.ExpenseValidationException
import com.example.expense_management_server.domain.expense.model.ExpenseDomainModel
import com.example.expense_management_server.domain.expense.port.IExpensePersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class ExpensePersistenceAdapter(
    private val expenseRepository: ExpenseRepository,
    private val balanceGroupRepository: BalanceGroupRepository
) : IExpensePersistencePort {

    override fun save(expenseData: ExpenseDomainModel): ExpenseDomainModel {
        val savedExpense = expenseRepository.save(map(expenseData))
        LOGGER.info { "New expense ${savedExpense.id} was successfully saved in database" }
        return map(savedExpense)
    }

    override fun update(
        expenseId: UUID,
        expenseUpdatedData: ExpenseDomainModel
    ): ExpenseDomainModel {
        val expense = expenseRepository.findById(expenseId)
            .orElseThrow { ExpenseNotFoundException(expenseId) }

        val savedExpense =
            expenseRepository.saveAndFlush(
                map(
                    expenseUpdatedData.copy(id = expenseId, createdAt = expense.createdAt),
                    expense.version
                )
            )
        LOGGER.info { "Expense ${savedExpense.id} was successfully updated" }
        return map(savedExpense)
    }

    override fun getById(expenseId: UUID): ExpenseDomainModel? {
        return expenseRepository.findById(expenseId)
            .map { map(it) }
            .getOrNull()
    }

    override fun getAllByBalanceGroup(balanceGroupId: UUID): List<ExpenseDomainModel> {
        val expenses = balanceGroupRepository.findById(balanceGroupId)
            .map { it.expenses }
            .orElseThrow { BalanceGroupNotFoundException(balanceGroupId) }
        return expenses.map { map(it) }
    }

    override fun delete(expenseId: UUID) {
        expenseRepository.deleteById(expenseId)
        LOGGER.info { "Expense $expenseId was removed" }
    }

    private fun map(expenseDomainModel: ExpenseDomainModel, version: Int? = null): ExpenseEntity = ExpenseEntity(
        id = expenseDomainModel.id,
        version = version,
        createdAt = expenseDomainModel.createdAt,
        updatedAt = expenseDomainModel.updatedAt,
        name = expenseDomainModel.name,
        amount = expenseDomainModel.amount,
        balanceGroup = balanceGroupRepository.findById(expenseDomainModel.balanceGroupId)
            .orElseThrow { ExpenseValidationException(message = "Balance group not found") },
        splitType = expenseDomainModel.splitType,
    )

    private fun map(expenseEntity: ExpenseEntity): ExpenseDomainModel {
        return ExpenseDomainModel(
            id = expenseEntity.id,
            name = expenseEntity.name,
            balanceGroupId = expenseEntity.balanceGroup.id!!,
            amount = expenseEntity.amount,
            splitType = expenseEntity.splitType,
            createdAt = expenseEntity.createdAt,
            updatedAt = expenseEntity.updatedAt,
            expenseOwnerId = expenseEntity.createdById!!
        )
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}
