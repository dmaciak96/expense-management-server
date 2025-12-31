package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.exception.UserNotAuthenticatedException
import com.example.expense_management_server.adapter.security.model.UserAccount
import com.example.expense_management_server.domain.service.BalanceService
import com.example.expense_management_server.domain.service.ExpenseService
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.domain.user.port.SecurityPort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.UUID


@Component
class SpringSecurityAdapter(
    private val balanceService: BalanceService,
    private val expenseService: ExpenseService,
) : SecurityPort {

    override fun getCurrentLoginUserId(): UUID {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotFoundException()
        if (!authentication.isAuthenticated) {
            throw UserNotAuthenticatedException()
        }
        val userDetails = authentication.principal as UserAccount
        return userDetails.id
    }

    override fun checkIfCurrentUserIsAdmin(): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        return account.role == UserRole.ADMIN
    }

    override fun checkIfCurrentUserIsBalanceGroupMember(balanceGroupId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        val balanceGroup = balanceService.getById(balanceGroupId)
        return balanceGroup.groupMemberIds.contains(account.id)
    }

    override fun checkIfCurrentUserIsBalanceGroupCreator(balanceGroupId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        val balanceGroup = balanceService.getById(balanceGroupId)
        return balanceGroup.groupOwnerUserId == account.id
    }

    override fun checkIfCurrentUserIsExpenseCreator(expenseId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        val expense = expenseService.getById(expenseId)
        return expense.expenseOwnerId == account.id
    }
}