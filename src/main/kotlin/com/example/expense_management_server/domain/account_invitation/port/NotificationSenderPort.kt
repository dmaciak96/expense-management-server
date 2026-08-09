package com.example.expense_management_server.domain.account_invitation.port

import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation

interface NotificationSenderPort {
    fun sendInvitationNotification(invitation: AccountMemberInvitation)
}