package com.example.expense_management_server.domain.port

import com.example.expense_management_server.domain.model.AccountMemberInvitation
import java.util.*

interface InvitationPersistencePort {
    fun create(invitation: AccountMemberInvitation): AccountMemberInvitation
    fun findById(id: UUID): AccountMemberInvitation
    fun update(invitation: AccountMemberInvitation): AccountMemberInvitation
    fun deleteById(id: UUID)
}