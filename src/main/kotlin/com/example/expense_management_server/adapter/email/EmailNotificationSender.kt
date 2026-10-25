package com.example.expense_management_server.adapter.email

import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.port.NotificationSenderPort
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.authentication.port.AuthenticationPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class EmailNotificationSender(
    private val mailSender: JavaMailSender,
    private val accountPersistencePort: AccountPersistencePort,
    private val authenticationPort: AuthenticationPort,
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

    override fun sendUserVerificationMessage(applicationUser: ApplicationUser) {
        LOGGER.info { "Send user account verification email to ${applicationUser.email}" }
        val verificationToken = authenticationPort.generateAccountVerificationToken(applicationUser)
        val appBaseUrl = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .build()
            .toUriString()
        val verifyUrl =
            "$appBaseUrl/auth/email/verify?token=${URLEncoder.encode(verificationToken, StandardCharsets.UTF_8)}"
        val subject = "Account activation"
        val body = """
            Hello, ${applicationUser.displayName ?: ""}!
            
            Thank you for signing up for Expense Management!
            
            Your account has been created successfully. To complete your registration and start using the
            application, please verify your email address by clicking the link below.
            
            $verifyUrl
            
            This activation link is valid for 15 minutes.
            
            Best regards,
            The Expense Management App Team
        """.trimIndent()
        sendPlainTextMessage(applicationUser.email, subject, body)
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