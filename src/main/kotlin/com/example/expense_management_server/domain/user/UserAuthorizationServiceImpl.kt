package com.example.expense_management_server.domain.user

import com.example.expense_management_server.domain.service.UserAuthorizationService
import com.example.expense_management_server.domain.user.port.UserAuthorizationPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service("userAuthorizationService")
class UserAuthorizationServiceImpl(
    private val userAuthorizationPort: UserAuthorizationPort
) : UserAuthorizationService {
    override fun getCurrentLoginUserId(): UUID {
        return userAuthorizationPort.getCurrentLoginUserId()
    }

    override fun checkIfCurrentUserIsAdmin(): Boolean {
        return userAuthorizationPort.checkIfCurrentUserIsAdmin()
    }

    override fun checkIfCurrentUserIsBalanceGroupMember(balanceGroupId: UUID): Boolean {
        return userAuthorizationPort.checkIfCurrentUserIsBalanceGroupMember(balanceGroupId)
    }

    override fun checkIfCurrentUserIsBalanceGroupCreator(balanceGroupId: UUID): Boolean {
        return userAuthorizationPort.checkIfCurrentUserIsBalanceGroupCreator(balanceGroupId)
    }

    override fun checkIfCurrentUserIsExpenseCreator(expenseId: UUID): Boolean {
        return userAuthorizationPort.checkIfCurrentUserIsExpenseCreator(expenseId)
    }

}