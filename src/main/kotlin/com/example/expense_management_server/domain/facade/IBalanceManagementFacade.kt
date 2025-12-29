package com.example.expense_management_server.domain.facade

import com.example.expense_management_server.domain.balance.model.BalanceGroup
import java.util.UUID

interface IBalanceManagementFacade {
    fun save(balanceGroup: BalanceGroup): BalanceGroup
    fun update(balanceGroupId: UUID, balanceGroup: BalanceGroup): BalanceGroup
    fun delete(balanceGroupId: UUID)
    fun getById(balanceGroupId: UUID): BalanceGroup
    fun getAllWhereUserIsGroupMember(userId: UUID): List<BalanceGroup>
}