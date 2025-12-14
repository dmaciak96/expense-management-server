package com.example.expense_management_server.domain.balancegroup.model

import java.time.OffsetDateTime
import java.util.UUID

data class BalanceGroupDomainModel(
    val id: UUID?,
    val groupName: String,
    val groupMemberIds: List<UUID>,
    val expenseIds: List<UUID>,
    val groupOwnerUserId: UUID,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?
)
