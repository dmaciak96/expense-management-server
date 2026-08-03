package com.example.expense_management_server.application.expense

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.exception.ExpenseValidationException
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.Expense
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class AddExpenseToAccountUseCase(
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase,
    private val accountPersistencePort: AccountPersistencePort,
) {

    fun execute(expenseToAdd: Expense, accountId: UUID): Account {
        LOGGER.info { "Adding new expense to account $accountId" }
        val account = fetchAccountByIdUseCase.execute(accountId)
        if (isNotAccountOwner(account, expenseToAdd.paidBy) && isNotAccountMember(account, expenseToAdd.paidBy)) {
            throw ExpenseValidationException("Expense is paid by user which not belongs to this account")
        }
        val updatedAccount = accountPersistencePort.addExpense(expenseToAdd, accountId)
        LOGGER.info { "Added new expense ${expenseToAdd.id} to account $accountId" }
        return updatedAccount
    }

    private fun isNotAccountOwner(account: Account, user: ApplicationUser) = account.createdBy.id != user.id
    private fun isNotAccountMember(account: Account, user: ApplicationUser) =
        !account.members.map { it.applicationUserId }
            .contains(user.id)

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}