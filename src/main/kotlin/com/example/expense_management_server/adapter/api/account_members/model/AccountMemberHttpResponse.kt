package com.example.expense_management_server.adapter.api.account_members.model

import com.example.expense_management_server.domain.account.model.AccountMember
import java.util.*

data class AccountMemberHttpResponse(
    val applicationUserId: UUID,
    val firstName: String,
    val lastName: String,
    val displayName: String?,
    val avatarUrl: String?,
) {
    companion object {
        fun fromDomain(accountMember: AccountMember) = AccountMemberHttpResponse(
            applicationUserId = accountMember.applicationUserId,
            firstName = accountMember.firstName,
            lastName = accountMember.lastName,
            displayName = accountMember.displayName,
            avatarUrl = accountMember.avatarUrl,
        )
    }
}
