package com.example.expense_management_server.domain.account.model

import java.util.*

data class AccountMember(
    val applicationUserId: UUID,
    val firstName: String,
    val lastName: String,
    val displayName: String?,
    val avatarUrl: String?,
)
