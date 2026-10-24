package com.example.expense_management_server.adapter.email

import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.port.NotificationSenderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailNotificationSender(
    private val mailSender: JavaMailSender,
    private val accountPersistencePort: AccountPersistencePort
) : NotificationSenderPort {
    override fun sendInvitationNotification(invitation: AccountMemberInvitation) {
        LOGGER.info { "Sending account ${invitation.accountId} invitation to ${invitation.email}" }
        val body = """
            You has been invited by ${invitation.createdBy.email} to participating with expense management.
            Account Name: ${accountPersistencePort.findById(invitation.accountId).name}
            To accept invitation click on the following link: DUMMY LINK
        """.trimIndent()
        val subject = "You has been invited to participating with expense management"
        sendPlainTextMessage(invitation.email, subject, body)

    }

    fun sendPlainTextMessage(to: String, subject: String, body: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.text = body
        mailSender.send(message)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}