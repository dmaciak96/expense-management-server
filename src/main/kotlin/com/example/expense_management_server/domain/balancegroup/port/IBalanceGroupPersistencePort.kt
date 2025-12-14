package com.example.expense_management_server.domain.balancegroup.port

import com.example.expense_management_server.domain.balancegroup.model.BalanceGroupDomainModel
import java.util.UUID

interface IBalanceGroupPersistencePort {
    fun save(balanceGroupData: BalanceGroupDomainModel): BalanceGroupDomainModel
    fun update(groupId: UUID, balanceGroupUpdatedData: BalanceGroupDomainModel): BalanceGroupDomainModel
    fun getById(groupId: UUID): BalanceGroupDomainModel?
    fun getAll(): List<BalanceGroupDomainModel>
    fun delete(groupId: UUID)
    fun getAllWhereUserIsGroupMember(userMemberId: UUID): List<BalanceGroupDomainModel>
}