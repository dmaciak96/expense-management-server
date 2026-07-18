package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.account.model.AccountMember
import java.util.*

data class AccountMemberEntity(
    val applicationUserId: UUID,
    val firstName: String,
    val lastName: String,
    val displayName: String?,
    val avatarUrl: String?,
) {
    fun toDomain() = AccountMember(
        applicationUserId = this.applicationUserId,
        firstName = this.firstName,
        lastName = this.lastName,
        displayName = this.displayName,
        avatarUrl = this.avatarUrl
    )

    companion object {
        fun fromDomain(domain: AccountMember) = AccountMemberEntity(
            applicationUserId = domain.applicationUserId,
            firstName = domain.firstName,
            lastName = domain.lastName,
            displayName = domain.displayName,
            avatarUrl = domain.avatarUrl,
        )
    }
}
