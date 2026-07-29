package com.example.expense_management_server.adapter.persistence.repository

import com.example.expense_management_server.adapter.persistence.model.AccountMemberInvitationEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvitationRepository : CrudRepository<AccountMemberInvitationEntity, String> {
    fun findAllByAccountId(accountId: String): List<AccountMemberInvitationEntity>
}