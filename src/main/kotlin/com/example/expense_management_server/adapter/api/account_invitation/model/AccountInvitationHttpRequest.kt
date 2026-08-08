package com.example.expense_management_server.adapter.api.account_invitation.model

import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import java.time.Instant
import java.util.*

data class AccountInvitationHttpRequest(
    val accountId: UUID,
    val email: String,
    val status: AccountMemberInvitationStatus
) {
    fun toDomain(
        id: UUID,
        createdAt: Instant,
        createdBy: ApplicationUser,
        expiresAt: Instant,
        acceptedAt: Instant? = null
    ) =
        AccountMemberInvitation(
            id = id,
            createdAt = createdAt,
            createdBy = createdBy,
            accountId = this.accountId,
            email = this.email,
            expiresAt = expiresAt,
            acceptedAt = acceptedAt,
            status = this.status
        )
}
