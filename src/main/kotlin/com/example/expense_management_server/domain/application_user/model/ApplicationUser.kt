package com.example.expense_management_server.domain.application_user.model

import com.example.expense_management_server.domain.account.model.AccountMember
import java.time.Instant
import java.util.*

data class ApplicationUser(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val password: String,
    val displayName: String?,
    val avatarUrl: String?,
    val status: ApplicationUserStatus,
) {
    fun toAccountMember() = AccountMember(
        applicationUserId = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        displayName = this.displayName,
        avatarUrl = this.avatarUrl,
    )
}
