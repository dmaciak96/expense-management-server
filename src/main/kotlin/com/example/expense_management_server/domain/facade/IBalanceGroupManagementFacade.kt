package com.example.expense_management_server.domain.facade

import com.example.expense_management_server.adapter.api.model.BalanceGroupRequest
import com.example.expense_management_server.domain.balancegroup.model.BalanceGroupDomainModel
import java.util.UUID

interface IBalanceGroupManagementFacade {
    fun save(balanceGroupDomainModel: BalanceGroupDomainModel): BalanceGroupDomainModel
    fun update(balanceGroupId: UUID, balanceGroupDomainModel: BalanceGroupDomainModel): BalanceGroupDomainModel
    fun delete(balanceGroupId: UUID)
    fun getById(balanceGroupId: UUID): BalanceGroupDomainModel
    fun getAllWhereUserIsGroupMember(userId: UUID): List<BalanceGroupDomainModel>
}