package com.example.expense_management_server.adapter.api.model

import com.example.expense_management_server.domain.balancegroup.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balancegroup.model.BalanceGroupDomainModel
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
        fun from(domainModel: BalanceGroupDomainModel): BalanceGroupResponse = BalanceGroupResponse(
            id = domainModel.id ?: throw BalanceGroupValidationException("id must not be null"),
            groupName = domainModel.groupName,
            groupMemberIds = domainModel.groupMemberIds,
            createdAt = domainModel.createdAt,
            updatedAt = domainModel.updatedAt
        )
    }
}
