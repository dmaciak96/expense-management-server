package com.example.expense_management_server.domain.balance.port

import com.example.expense_management_server.domain.balance.model.BalanceGroup
import java.util.UUID

interface BalanceGroupPersistencePort {
    fun save(balanceGroup: BalanceGroup): BalanceGroup
    fun update(balanceGroupId: UUID, balanceGroup: BalanceGroup): BalanceGroup
    fun getById(balanceGroupId: UUID): BalanceGroup?
    fun getAll(): List<BalanceGroup>
    fun delete(balanceGroupId: UUID)
    fun getAllWhereUserIsGroupMember(balanceGroupMemberId: UUID): List<BalanceGroup>
}