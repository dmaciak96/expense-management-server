package com.example.expense_management_server.domain.user.port

import com.example.expense_management_server.domain.user.model.UserDomainModel
import java.util.UUID

interface ISecurityPort {
    fun getCurrentLoginUser(): UserDomainModel
    fun isAdmin(): Boolean
    fun isBalanceGroupMember(balanceGroupId: UUID): Boolean
    fun isBalanceGroupCreator(balanceGroupId: UUID): Boolean
    fun isExpenseCreator(expenseId: UUID): Boolean
}