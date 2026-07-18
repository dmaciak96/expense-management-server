package com.example.expense_management_server.domain.account_invitation.model

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import java.time.Instant
import java.util.*

data class AccountMemberInvitation(
    val id: UUID,
    val createdAt: Instant,
    val createdBy: ApplicationUser,
    val accountId: UUID,
    val email: String,
    val token: String,
    val expiresAt: Instant,
    val acceptedAt: Instant? = null,
    val status: AccountMemberInvitationStatus
)
