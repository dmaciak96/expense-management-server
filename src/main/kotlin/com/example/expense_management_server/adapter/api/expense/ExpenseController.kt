package com.example.expense_management_server.adapter.api.expense

import com.example.expense_management_server.adapter.api.account.model.AccountHttpResponse
import com.example.expense_management_server.adapter.api.expense.model.ExpenseHttpRequest
import com.example.expense_management_server.adapter.api.expense.model.ExpenseHttpResponse
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.application.expense.AddExpenseToAccountUseCase
import com.example.expense_management_server.application.expense.DeleteExpenseFromAccountUseCase
import com.example.expense_management_server.application.expense.FetchAllExpensesFromAccount
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/accounts/{accountId}/expenses")
class ExpenseController(
    private val fetchUserByEmailUseCase: FetchUserByEmailUseCase,
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val addExpenseToAccountUseCase: AddExpenseToAccountUseCase,
    private val deleteExpenseFromAccountUseCase: DeleteExpenseFromAccountUseCase,
    private val fetchAllExpensesFromAccount: FetchAllExpensesFromAccount
) {

    @PostMapping
    fun addNewExpenseToAccount(
        @PathVariable accountId: UUID,
        @Valid @RequestBody expenseRequest: ExpenseHttpRequest
    ): AccountHttpResponse {
        val createdBy = fetchCurrentLoginUserUseCase.execute()
        val paidBy = fetchUserByEmailUseCase.execute(expenseRequest.paidByEmail)
        val expenseToAdd = expenseRequest.toDomain(createdBy, paidBy)
        val updatedAccount = addExpenseToAccountUseCase.execute(expenseToAdd, accountId)
        return AccountHttpResponse.fromDomain(updatedAccount)
    }

    @DeleteMapping("/{expenseId}")
    fun deleteExpenseFromAccount(@PathVariable accountId: UUID, @PathVariable expenseId: UUID): AccountHttpResponse {
        val updatedAccount = deleteExpenseFromAccountUseCase.execute(expenseId, accountId)
        return AccountHttpResponse.fromDomain(updatedAccount)
    }

    @GetMapping
    fun getExpensesByAccountId(@PathVariable accountId: UUID): List<ExpenseHttpResponse> {
        return fetchAllExpensesFromAccount.execute(accountId)
            .map { ExpenseHttpResponse.fromDomain(it) }
    }
}