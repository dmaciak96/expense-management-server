package com.example.expense_management_server.domain.account_invitation.port

import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.application_user.model.ApplicationUser

interface NotificationSenderPort {
    fun sendInvitationNotification(invitation: AccountMemberInvitation)
    fun sendUserVerificationMessage(applicationUser: ApplicationUser)
}