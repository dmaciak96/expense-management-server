package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.domain.account_invitation.exception.InvitationValidationException
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class UpdateAccountInvitationStatusUseCase(
    private val invitationPersistencePort: InvitationPersistencePort
) {

    fun execute(invitationId: UUID, newStatus: AccountMemberInvitationStatus): AccountMemberInvitation {
        LOGGER.info { "Changing invitation status to $newStatus" }
        val invitation = invitationPersistencePort.findById(invitationId)
        if (invitation.status != AccountMemberInvitationStatus.PENDING) {
            throw InvitationValidationException("The status of the invitation can be changed only for PENDING invitations.")
        }
        val updatedInvitation = invitationPersistencePort.updateStatus(invitationId, newStatus)
        LOGGER.info { "Invitation $invitationId status was updated from ${invitation.status} to ${updatedInvitation.status}" }
        return updatedInvitation
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}