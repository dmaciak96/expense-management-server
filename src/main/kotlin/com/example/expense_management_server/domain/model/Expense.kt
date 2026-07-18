package com.example.expense_management_server.domain.model

import java.time.Instant
import java.util.*

data class Expense(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val createdBy: ApplicationUser,
    val paidBy: ApplicationUser,
    val name: String,
    val monetaryAmount: Long
)
