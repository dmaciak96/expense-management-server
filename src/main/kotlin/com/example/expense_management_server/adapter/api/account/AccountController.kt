package com.example.expense_management_server.adapter.api.account

import com.example.expense_management_server.adapter.api.account.model.AccountCreationHttpRequest
import com.example.expense_management_server.adapter.api.account.model.AccountHttpResponse
import com.example.expense_management_server.adapter.api.account.model.AccountUpdateHttpRequest
import com.example.expense_management_server.application.account.AccountCreationUseCase
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.application.account.FetchAllAccountsUseCase
import com.example.expense_management_server.application.account.RemoveAccountUseCase
import com.example.expense_management_server.application.account.UpdateAccountUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/accounts")
class AccountController(
    private val accountCreationUseCase: AccountCreationUseCase,
    private val removeAccountUseCase: RemoveAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val fetchAllAccountsUseCase: FetchAllAccountsUseCase,
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase,
) {

    @PostMapping
    fun createNewAccount(@Valid @RequestBody accountRequest: AccountCreationHttpRequest): AccountHttpResponse {
        val createdAccount = accountCreationUseCase.execute(accountRequest.toDomain())
        return AccountHttpResponse.fromDomain(createdAccount)
    }

    @DeleteMapping("/{id}")
    fun deleteAccount(@PathVariable id: UUID) {
        removeAccountUseCase.execute(id)
    }

    @PutMapping("/{id}")
    fun updateAccount(
        @PathVariable id: UUID,
        @Valid @RequestBody accountRequest: AccountUpdateHttpRequest
    ): AccountHttpResponse {
        val updatedAccount = updateAccountUseCase.execute(accountRequest.toDomain(id))
        return AccountHttpResponse.fromDomain(updatedAccount)
    }

    @GetMapping
    fun getAllAccountsWhereOwnerOrMember(): List<AccountHttpResponse> {
        return fetchAllAccountsUseCase.execute()
            .map { AccountHttpResponse.fromDomain(it) }
    }

    @GetMapping("/{id}")
    fun getAccountById(@PathVariable id: UUID): AccountHttpResponse {
        val account = fetchAccountByIdUseCase.execute(id)
        return AccountHttpResponse.fromDomain(account)
    }
}