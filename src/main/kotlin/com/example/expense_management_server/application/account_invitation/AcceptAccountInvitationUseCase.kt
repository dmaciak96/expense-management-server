package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.application.account_members.AddMemberToAccountUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account_invitation.exception.InvitationExpiredException
import com.example.expense_management_server.domain.account_invitation.exception.InvitationNotFoundException
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class AcceptAccountInvitationUseCase(
    private val currentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val invitationPersistencePort: InvitationPersistencePort,
    private val addMemberToAccountUseCase: AddMemberToAccountUseCase
) {

    fun execute(invitationId: UUID): Account {
        val invitation = invitationPersistencePort.findById(invitationId)
        val currentUser = currentLoginUserUseCase.execute()
        if (currentUser.email != invitation.email) {
            throw InvitationNotFoundException("Invitation $invitationId not found for ${currentUser.email}")
        }
        if (invitation.status == AccountMemberInvitationStatus.EXPIRED) {
            throw InvitationExpiredException("Invitation expired")
        }
        invitationPersistencePort.updateStatus(invitationId, AccountMemberInvitationStatus.ACCEPTED)
        LOGGER.info { "Invitation $invitationId was accepted by ${currentUser.email}. Adding user to account $${invitation.accountId}" }
        return addMemberToAccountUseCase.execute(accountId = invitation.accountId, applicationUserId = currentUser.id)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}