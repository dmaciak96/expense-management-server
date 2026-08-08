package com.example.expense_management_server.adapter.email

import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.port.NotificationSenderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class EmailNotificationSender: NotificationSenderPort {
    override fun sendInvitationNotification(invitation: AccountMemberInvitation) {
        LOGGER.info { "Sent invitation ${invitation.accountId} to ${invitation.email}" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}