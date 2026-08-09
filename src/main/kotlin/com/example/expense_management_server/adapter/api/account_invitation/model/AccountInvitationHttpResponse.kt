package com.example.expense_management_server.adapter.api.account_invitation.model

import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserHttpResponse
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import java.time.Instant
import java.util.*

data class AccountInvitationHttpResponse(
    val id: UUID,
    val createdAt: Instant,
    val createdBy: ApplicationUserHttpResponse,
    val accountId: UUID,
    val email: String,
    val expiresAt: Instant,
    val acceptedAt: Instant? = null,
    val status: AccountMemberInvitationStatus
) {
    companion object {
        fun fromDomain(accountInvitation: AccountMemberInvitation) = AccountInvitationHttpResponse(
            id = accountInvitation.id,
            createdAt = accountInvitation.createdAt,
            createdBy = ApplicationUserHttpResponse.fromDomain(accountInvitation.createdBy),
            accountId = accountInvitation.accountId,
            email = accountInvitation.email,
            expiresAt = accountInvitation.expiresAt,
            acceptedAt = accountInvitation.acceptedAt,
            status = accountInvitation.status
        )
    }
}
