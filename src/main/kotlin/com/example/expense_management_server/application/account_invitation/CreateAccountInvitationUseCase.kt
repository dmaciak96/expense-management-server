package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import com.example.expense_management_server.domain.account_invitation.port.NotificationSenderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CreateAccountInvitationUseCase(
    private val invitationPersistencePort: InvitationPersistencePort,
    private val accountPersistencePort: AccountPersistencePort,
    private val notificationSenderPort: NotificationSenderPort,
    private val fetchUserByEmailUseCase: FetchUserByEmailUseCase
) {

    fun execute(invitation: AccountMemberInvitation): AccountMemberInvitation {
        val account = accountPersistencePort.findById(invitation.accountId)
        if (invitation.createdBy.id != account.createdBy.id) {
            throw AccountNotFoundException("Account ${invitation.accountId} not found")
        }
        fetchUserByEmailUseCase.execute(invitation.email)
        val savedInvitation = invitationPersistencePort.create(invitation)
        LOGGER.info { "Saved invitation: ${savedInvitation.id}" }
        notificationSenderPort.sendInvitationNotification(invitation)
        return savedInvitation
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}