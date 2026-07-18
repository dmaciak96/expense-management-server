package com.example.expense_management_server.domain.model

import java.time.Instant
import java.util.UUID

data class Account(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val createdBy: ApplicationUser,
    val name: String,
    val currency: Currency,
    val groupMembers: Set<ApplicationUser>,
    val expenses: Set<Expense>,
    val status: AccountStatus,
)
