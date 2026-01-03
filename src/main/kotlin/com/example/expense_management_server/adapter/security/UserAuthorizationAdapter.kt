package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.exception.UserNotAuthenticatedException
import com.example.expense_management_server.adapter.security.model.UserAccount
import com.example.expense_management_server.domain.service.BalanceService
import com.example.expense_management_server.domain.service.ExpenseService
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.domain.user.port.UserAuthorizationPort
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component
import java.util.UUID


@Component
class UserAuthorizationAdapter(
    private val balanceService: BalanceService,
    private val expenseService: ExpenseService,
) : UserAuthorizationPort {

    override fun getCurrentLoginUserId(): UUID {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotFoundException()
        if (!authentication.isAuthenticated) {
            throw UserNotAuthenticatedException()
        }
        return getUserId(authentication)
    }

    override fun checkIfCurrentUserIsAdmin(): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val roles = getUserRoles(authentication)
        return roles.contains(UserRole.ADMIN.name)
    }

    override fun checkIfCurrentUserIsBalanceGroupMember(balanceGroupId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val balanceGroup = balanceService.getById(balanceGroupId)
        return balanceGroup.groupMemberIds.contains(getUserId(authentication))
    }

    override fun checkIfCurrentUserIsBalanceGroupCreator(balanceGroupId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val balanceGroup = balanceService.getById(balanceGroupId)
        return balanceGroup.groupOwnerUserId == getUserId(authentication)
    }

    override fun checkIfCurrentUserIsExpenseCreator(expenseId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val expense = expenseService.getById(expenseId)
        return expense.expenseOwnerId == getUserId(authentication)
    }

    private fun getUserId(authentication: Authentication): UUID {
        if (authentication.principal is Jwt) {
            val token = authentication.principal as Jwt
            return UUID.fromString(token.subject)
        } else if (authentication.principal is UserAccount) {
            val account = authentication.principal as UserAccount
            return account.id
        }
        throw IllegalArgumentException("Unsupported principal type")
    }

    private fun getUserRoles(authentication: Authentication): List<String> {
        if (authentication.principal is Jwt) {
            val token = authentication.principal as Jwt
            return token.claims["roles"] as List<String>
        } else if (authentication.principal is UserAccount) {
            val account = authentication.principal as UserAccount
            return listOf(account.role.name)
        }
        throw IllegalArgumentException("Unsupported principal type")
    }
}