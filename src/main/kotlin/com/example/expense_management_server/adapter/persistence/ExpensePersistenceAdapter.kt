package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.ExpenseEntity
import com.example.expense_management_server.adapter.persistence.repository.BalanceGroupRepository
import com.example.expense_management_server.adapter.persistence.repository.ExpenseRepository
import com.example.expense_management_server.domain.balance.exception.BalanceGroupNotFoundException
import com.example.expense_management_server.domain.expense.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.expense.exception.ExpenseValidationException
import com.example.expense_management_server.domain.expense.model.Expense
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

    override fun save(expense: Expense): Expense {
        val savedEntity = expenseRepository.save(map(expense))
        LOGGER.info { "New expense ${savedEntity.id} was successfully saved in database" }
        return map(savedEntity)
    }

    override fun update(
        expenseId: UUID,
        expense: Expense
    ): Expense {
        val targetEntity = expenseRepository.findById(expenseId)
            .orElseThrow { ExpenseNotFoundException(expenseId) }

        val updatedEntity =
            expenseRepository.saveAndFlush(
                map(
                    expense.copy(id = expenseId, createdAt = targetEntity.createdAt),
                    targetEntity.version
                )
            )
        LOGGER.info { "Expense ${updatedEntity.id} was successfully updated" }
        return map(updatedEntity)
    }

    override fun getById(expenseId: UUID): Expense? {
        return expenseRepository.findById(expenseId)
            .map { map(it) }
            .getOrNull()
    }

    override fun getAllByBalanceGroup(balanceGroupId: UUID): List<Expense> {
        val expenses = balanceGroupRepository.findById(balanceGroupId)
            .map { it.expenses }
            .orElseThrow { BalanceGroupNotFoundException(balanceGroupId) }
        return expenses.map { map(it) }
    }

    override fun delete(expenseId: UUID) {
        expenseRepository.deleteById(expenseId)
        LOGGER.info { "Expense $expenseId was removed" }
    }

    private fun map(expense: Expense, version: Int? = null): ExpenseEntity = ExpenseEntity(
        id = expense.id,
        version = version,
        createdAt = expense.createdAt,
        updatedAt = expense.updatedAt,
        name = expense.name,
        amount = expense.amount,
        balanceGroup = balanceGroupRepository.findById(expense.balanceGroupId)
            .orElseThrow { ExpenseValidationException(message = "Balance group not found") },
        splitType = expense.splitType,
    )

    private fun map(expenseEntity: ExpenseEntity): Expense {
        return Expense(
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
