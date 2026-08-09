package com.example.expense_management_server.adapter.api.account_invitation.model

import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus

data class AccountInvitationStatusUpdateHttpRequest(
    val status: AccountMemberInvitationStatus
)
