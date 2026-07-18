package com.example.expense_management_server.domain.account_invitation.port

import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import java.util.*

interface InvitationPersistencePort {
    fun create(invitation: AccountMemberInvitation): AccountMemberInvitation
    fun findById(id: UUID): AccountMemberInvitation
    fun findAllByAccountId(accountId: UUID): List<AccountMemberInvitation>
    fun updateStatus(id: UUID, newStatus: AccountMemberInvitationStatus): AccountMemberInvitation
    fun deleteById(id: UUID)
}