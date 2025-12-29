package com.example.expense_management_server.adapter.api.model

import com.example.expense_management_server.domain.balance.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balance.model.BalanceGroup
import java.time.OffsetDateTime
import java.util.UUID

data class BalanceGroupResponse(
    val id: UUID,
    val groupName: String,
    val groupMemberIds: List<UUID>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime?
) {
    companion object {
        fun from(balanceGroup: BalanceGroup): BalanceGroupResponse = BalanceGroupResponse(
            id = balanceGroup.id ?: throw BalanceGroupValidationException("id must not be null"),
            groupName = balanceGroup.groupName,
            groupMemberIds = balanceGroup.groupMemberIds,
            createdAt = balanceGroup.createdAt,
            updatedAt = balanceGroup.updatedAt
        )
    }
}
