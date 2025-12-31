package com.example.expense_management_server.domain.user

import com.example.expense_management_server.domain.facade.UserAuthorizationService
import com.example.expense_management_server.domain.user.port.ISecurityPort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserAuthorizationServiceImpl(
    private val securityPort: ISecurityPort
) : UserAuthorizationService {
    override fun getCurrentLoginUserId(): UUID {
        return securityPort.getCurrentLoginUserId()
    }

    override fun checkIfCurrentUserIsAdmin(): Boolean {
        return securityPort.checkIfCurrentUserIsAdmin()
    }

    override fun checkIfCurrentUserIsBalanceGroupMember(balanceGroupId: UUID): Boolean {
        return securityPort.checkIfCurrentUserIsBalanceGroupMember(balanceGroupId)
    }

    override fun checkIfCurrentUserIsBalanceGroupCreator(balanceGroupId: UUID): Boolean {
        return securityPort.checkIfCurrentUserIsBalanceGroupCreator(balanceGroupId)
    }

    override fun checkIfCurrentUserIsExpenseCreator(expenseId: UUID): Boolean {
        return securityPort.checkIfCurrentUserIsExpenseCreator(expenseId)
    }

}