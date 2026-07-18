package com.example.expense_management_server.domain.model

import java.time.Instant
import java.util.UUID

data class AccountMemberInvitation(
    val id: UUID,
    val createdAt: Instant,
    val createdBy: ApplicationUser,
    val accountId: UUID,
    val email: String,
    val token: String,
    val expiresAt: Instant,
    val acceptedAt: Instant,
    val status: AccountMemberInvitationStatus
)
