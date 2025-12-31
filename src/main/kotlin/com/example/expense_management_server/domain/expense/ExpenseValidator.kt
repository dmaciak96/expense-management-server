package com.example.expense_management_server.domain.expense

import com.example.expense_management_server.domain.balance.port.BalanceGroupPersistencePort
import com.example.expense_management_server.domain.expense.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.expense.exception.ExpenseValidationException
import com.example.expense_management_server.domain.expense.model.Expense
import com.example.expense_management_server.domain.expense.port.ExpensePersistencePort
import com.example.expense_management_server.domain.user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ExpenseValidator(
    private val expensePersistencePort: ExpensePersistencePort,
    private val balanceGroupPersistencePort: BalanceGroupPersistencePort,
    private val userPersistencePort: UserPersistencePort,
) {

    fun validate(expense: Expense) {
        checkIfOwnerExists(expense.expenseOwnerId)
        checkBalanceGroupExists(expense.balanceGroupId)
        checkExpenseOwnerIsGroupMember(expense.balanceGroupId, expense.expenseOwnerId)
        checkIfNameIsBlank(expense.name)
        checkIfAmountIsLessThanZero(expense.amount)
    }

    fun validateForUpdate(expenseId: UUID, expense: Expense) {
        checkIfExpenseExists(expenseId)
        validate(expense)
        val existingExpense = expensePersistencePort.getById(expenseId)!!
        checkIfBalanceGroupWasChanged(existingExpense, expense)
        checkIfExpenseOwnerWasChanged(existingExpense, expense)
    }

    fun checkIfExpenseExists(expenseId: UUID) {
        if (expensePersistencePort.getById(expenseId) == null) {
            LOGGER.debug { "Expense $expenseId not found" }
            throw ExpenseNotFoundException(expenseId)
        }
        LOGGER.debug { "Expense $expenseId exists" }
    }

    fun checkBalanceGroupExists(balanceGroupId: UUID) {
        val balanceGroup = balanceGroupPersistencePort.getById(balanceGroupId)
        if (balanceGroup == null) {
            LOGGER.debug { "Balance group ($balanceGroupId) not found, expense cannot be created" }
            throw ExpenseValidationException("Balance group does not exist")
        }
        LOGGER.debug { "BalanceGroup ($balanceGroupId) exists" }
    }

    private fun checkExpenseOwnerIsGroupMember(balanceGroupId: UUID, expenseOwnerId: UUID) {
        val balanceGroup = balanceGroupPersistencePort.getById(balanceGroupId)!!
        if (!balanceGroup.groupMemberIds.contains(expenseOwnerId)) {
            LOGGER.debug { "Expense creator ($expenseOwnerId) is not a member of balance group ($balanceGroupId)" }
            throw ExpenseValidationException("Expense creator is not a member of balance group ($balanceGroupId), expense cannot be created")
        }
        LOGGER.debug { "Expense creator is valid balance group member ($balanceGroupId)" }
    }

    private fun checkIfBalanceGroupWasChanged(
        existingExpense: Expense,
        expense: Expense
    ) {
        if (existingExpense.balanceGroupId != expense.balanceGroupId) {
            LOGGER.debug { "Tried to change balance group for expense ${existingExpense.id}. This is not supported" }
            throw ExpenseValidationException("Moving expenses between balance groups is not supported")
        }
    }

    private fun checkIfExpenseOwnerWasChanged(
        existingExpense: Expense,
        expense: Expense
    ) {
        if (existingExpense.expenseOwnerId != expense.expenseOwnerId) {
            LOGGER.debug { "Tried to change expense owner for expense ${existingExpense.id}. This is not supported" }
            throw ExpenseValidationException("Changing expense owner for expense ${existingExpense.id}")
        }
    }

    private fun checkIfOwnerExists(ownerId: UUID) {
        val owner = userPersistencePort.findUserAccountById(ownerId)
        if (owner == null) {
            LOGGER.debug { "Expense owner ($ownerId) not found" }
            throw ExpenseValidationException("Expense owner does not exist")
        }
    }

    private fun checkIfNameIsBlank(name: String) {
        if (name.isBlank()) {
            LOGGER.debug { "Expense name ($name) is blank, expense cannot be created" }
            throw ExpenseValidationException("Expense name cannot contains whitespaces only")
        }
        LOGGER.debug { "Expense name ($name) is valid" }
    }

    private fun checkIfAmountIsLessThanZero(amount: Double) {
        if (amount <= 0.0) {
            LOGGER.debug { "Expense amount ($amount) is less than zero, expense cannot be created" }
            throw ExpenseValidationException("Amount must be positive value")
        }
        LOGGER.debug { "Expense amount ($amount) is valid" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}