package com.example.expense_management_server.application.expense

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class DeleteExpenseFromAccountUseCase(
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase,
    private val userPersistencePort: AccountPersistencePort,
) {
    fun execute(expenseId: UUID, accountId: UUID): Account {
        LOGGER.info { "Deleting expense $expenseId from account $accountId" }
        val account = fetchAccountByIdUseCase.execute(accountId)
        val expenseToRemove = account.expenses.filter { it.id == expenseId }
        if (expenseToRemove.isEmpty()) {
            throw ExpenseNotFoundException("Expense not found")
        }
        val updatedAccount = userPersistencePort.removeExpense(accountId, expenseId)
        LOGGER.info { "Expense $expenseId deleted successfully" }
        return updatedAccount
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}