package com.example.expense_management_server.domain.model

import java.time.Instant
import java.util.UUID

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
)
