package com.example.expense_management_server.domain.authentication.model

import java.time.Instant
import java.util.*

data class RefreshToken(
    val id: UUID,
    val createdAt: Instant,
    val expireAt: Instant,
    val applicationUserId: UUID,
    val token: UUID,
)
