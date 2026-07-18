package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.AccountMemberInvitationEntity
import com.example.expense_management_server.adapter.persistence.repository.InvitationRepository
import com.example.expense_management_server.domain.account_invitation.exception.InvitationNotFoundException
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class InvitationPersistenceAdapter(
    private val invitationRepository: InvitationRepository
) : InvitationPersistencePort {

    override fun create(invitation: AccountMemberInvitation): AccountMemberInvitation {
        LOGGER.debug { "Saving account invitation in DB $invitation" }
        val savedEntity = invitationRepository.save(AccountMemberInvitationEntity.fromDomain(invitation))
        val savedDomain = savedEntity.toDomain()
        LOGGER.debug { "Saved new account invitation in DB $savedDomain" }
        return savedDomain
    }

    override fun findById(id: UUID): AccountMemberInvitation {
        return invitationRepository.findById(id)
            .map {
                val domain = it.toDomain()
                LOGGER.debug { "Account invitation founded by it's ID: $domain" }
                return@map domain
            }
            .orElseThrow { InvitationNotFoundException("Invitation not found with specified ID: $id") }
    }

    override fun findAllByAccountId(accountId: UUID): List<AccountMemberInvitation> {
        LOGGER.debug { "Searching invitations by account ID: $accountId" }
        return invitationRepository.findAllByAccountId(accountId)
            .map { it.toDomain() }
    }

    override fun updateStatus(id: UUID, newStatus: AccountMemberInvitationStatus): AccountMemberInvitation {
        val invitation = invitationRepository.findById(id)
            .orElseThrow { InvitationNotFoundException("Invitation not found with specified ID: $id") }
        LOGGER.debug { "Updating invitation status from ${invitation.status} to $newStatus" }
        val updatedEntity = invitationRepository.save(invitation.copy(status = newStatus))
        val domain = updatedEntity.toDomain()
        LOGGER.debug { "Invitation status updated successfully $domain" }
        return domain
    }

    override fun deleteById(id: UUID) {
        invitationRepository.findById(id)
            .orElseThrow { InvitationNotFoundException("Invitation not found with specified ID: $id") }
        LOGGER.debug { "Deleting invitation $id from DB" }
        invitationRepository.deleteById(id)
        LOGGER.debug { "Invitation $id was deleted" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}