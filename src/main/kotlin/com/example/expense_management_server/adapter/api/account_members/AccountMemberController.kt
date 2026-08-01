package com.example.expense_management_server.adapter.api.account_members

import com.example.expense_management_server.adapter.api.account.model.AccountHttpResponse
import com.example.expense_management_server.adapter.api.account_members.model.AccountMemberHttpResponse
import com.example.expense_management_server.application.account_members.AddMemberToAccountUseCase
import com.example.expense_management_server.application.account_members.DeleteMemberFromAccountUseCase
import com.example.expense_management_server.application.account_members.FetchAllMembersFromAccountUseCase
import com.example.expense_management_server.application.account_members.FetchMemberByIdAndAccountIdUseCase
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/accounts/{accountId}/members")
class AccountMemberController(
    private val addMemberToAccountUseCase: AddMemberToAccountUseCase,
    private val deleteMemberFromAccountUseCase: DeleteMemberFromAccountUseCase,
    private val fetchAllMembersFromAccountUseCase: FetchAllMembersFromAccountUseCase,
    private val fetchMemberByIdAndAccountIdUseCase: FetchMemberByIdAndAccountIdUseCase
) {

    @PostMapping("/{applicationUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMemberToAccount(
        @PathVariable accountId: UUID,
        @PathVariable applicationUserId: UUID,
    ): AccountHttpResponse {
        val account = addMemberToAccountUseCase.execute(accountId, applicationUserId)
        return AccountHttpResponse.fromDomain(account)
    }

    @DeleteMapping("/{applicationUserId}")
    fun deleteMemberFromAccount(
        @PathVariable accountId: UUID,
        @PathVariable applicationUserId: UUID
    ): AccountHttpResponse {
        val account = deleteMemberFromAccountUseCase.execute(accountId, applicationUserId)
        return AccountHttpResponse.fromDomain(account)
    }

    @GetMapping
    fun getAllMembersFromAccount(@PathVariable accountId: UUID): List<AccountMemberHttpResponse> {
        val members = fetchAllMembersFromAccountUseCase.execute(accountId)
        return members.map { AccountMemberHttpResponse.fromDomain(it) }
    }

    @GetMapping("/{applicationUserId}")
    fun getMemberByAccountIdAndMemberId(
        @PathVariable accountId: UUID,
        @PathVariable applicationUserId: UUID
    ): AccountMemberHttpResponse {
        val member = fetchMemberByIdAndAccountIdUseCase.execute(accountId, applicationUserId)
        return AccountMemberHttpResponse.fromDomain(member)
    }
}