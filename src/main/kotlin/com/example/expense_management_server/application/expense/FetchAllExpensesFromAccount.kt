package com.example.expense_management_server.application.expense

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.model.Expense
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class FetchAllExpensesFromAccount(
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase
) {

    fun execute(accountId: UUID): List<Expense> {
        LOGGER.info { "Fetching all expenses from account $accountId" }
        val account = fetchAccountByIdUseCase.execute(accountId)
        LOGGER.info { "${account.expenses.size} expenses was found" }
        return account.expenses.toList()
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}