package com.example.expense_management_server.domain.account.model

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import java.time.Instant
import java.util.*

data class Account(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val createdBy: ApplicationUser?,
    val name: String,
    val currency: Currency,
    val members: Set<AccountMember>,
    val expenses: Set<Expense>,
    val status: AccountStatus,
)
