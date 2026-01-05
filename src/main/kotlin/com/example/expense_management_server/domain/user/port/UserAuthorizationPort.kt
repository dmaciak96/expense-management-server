package com.example.expense_management_server.domain.user.port

import java.util.UUID

interface UserAuthorizationPort {
    fun getCurrentLoginUserId(): UUID
    fun checkIfCurrentUserIsAdmin(): Boolean
    fun checkIfCurrentUserIsBalanceGroupMember(balanceGroupId: UUID): Boolean
    fun checkIfCurrentUserIsBalanceGroupCreator(balanceGroupId: UUID): Boolean
    fun checkIfCurrentUserIsExpenseCreator(expenseId: UUID): Boolean
}
