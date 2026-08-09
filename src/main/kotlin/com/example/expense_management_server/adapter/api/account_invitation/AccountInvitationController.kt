package com.example.expense_management_server.adapter.api.account_invitation

import com.example.expense_management_server.adapter.api.account.model.AccountHttpResponse
import com.example.expense_management_server.adapter.api.account_invitation.model.AccountInvitationHttpRequest
import com.example.expense_management_server.adapter.api.account_invitation.model.AccountInvitationHttpResponse
import com.example.expense_management_server.adapter.api.account_invitation.model.AccountInvitationStatusUpdateHttpRequest
import com.example.expense_management_server.application.account_invitation.AcceptAccountInvitationUseCase
import com.example.expense_management_server.application.account_invitation.CreateAccountInvitationUseCase
import com.example.expense_management_server.application.account_invitation.FetchAccountInvitationsByAccountIdUseCase
import com.example.expense_management_server.application.account_invitation.UpdateAccountInvitationStatusUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@RestController
@RequestMapping("/account-invitations")
class AccountInvitationController(
    private val acceptAccountInvitationUseCase: AcceptAccountInvitationUseCase,
    private val createAccountInvitationUseCase: CreateAccountInvitationUseCase,
    private val fetchAccountInvitationsByAccountIdUseCase: FetchAccountInvitationsByAccountIdUseCase,
    private val updateAccountInvitationStatusUseCase: UpdateAccountInvitationStatusUseCase,
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase
) {

    @PostMapping
    fun createAccountInvitation(@Valid @RequestBody request: AccountInvitationHttpRequest): AccountInvitationHttpResponse {
        val invitation = request.toDomain(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            createdBy = fetchCurrentLoginUserUseCase.execute(),
            expiresAt = OffsetDateTime.now()
                .plusMonths(1)
                .toInstant()
        )

        val createdInvitation = createAccountInvitationUseCase.execute(invitation)
        return AccountInvitationHttpResponse.fromDomain(createdInvitation)
    }

    @GetMapping
    fun getAccountInvitationsByAccountId(@RequestParam accountId: UUID): List<AccountInvitationHttpResponse> {
        return fetchAccountInvitationsByAccountIdUseCase.execute(accountId)
            .map { AccountInvitationHttpResponse.fromDomain(it) }
    }

    @PostMapping("/{id}")
    fun acceptAccountInvitation(@PathVariable id: UUID): AccountHttpResponse {
        val account = acceptAccountInvitationUseCase.execute(id)
        return AccountHttpResponse.fromDomain(account)
    }

    @PutMapping("/{id}")
    fun updateStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody request: AccountInvitationStatusUpdateHttpRequest
    ): AccountInvitationHttpResponse {
        val invitation = updateAccountInvitationStatusUseCase.execute(invitationId = id, newStatus = request.status)
        return AccountInvitationHttpResponse.fromDomain(invitation)
    }
}