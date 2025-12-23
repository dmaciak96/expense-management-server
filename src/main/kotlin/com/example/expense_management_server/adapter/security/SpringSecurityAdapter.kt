package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.exception.UserNotAuthenticatedException
import com.example.expense_management_server.adapter.security.model.UserAccount
import com.example.expense_management_server.domain.facade.IBalanceGroupManagementFacade
import com.example.expense_management_server.domain.facade.IExpenseManagementFacade
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.domain.user.port.ISecurityPort
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.UUID


@Component
class SpringSecurityAdapter(
    private val userPersistencePort: IUserPersistencePort,
    private val balanceGroupFacade: IBalanceGroupManagementFacade,
    private val expenseFacade: IExpenseManagementFacade
) : ISecurityPort {

    override fun getCurrentLoginUser(): UserDomainModel {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication
        if (authentication == null || !authentication.isAuthenticated) {
            throw UserNotAuthenticatedException()
        }
        val userDetails = authentication.principal as UserDetails
        return userPersistencePort.findUserAccountByEmail(userDetails.username) ?: throw UserNotFoundException()
    }

    override fun isAdmin(): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        return account.role == UserRole.ADMIN
    }

    override fun isBalanceGroupMember(balanceGroupId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        val balanceGroup = balanceGroupFacade.getById(balanceGroupId)
        return balanceGroup.groupMemberIds.contains(account.id)
    }

    override fun isBalanceGroupCreator(balanceGroupId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        val balanceGroup = balanceGroupFacade.getById(balanceGroupId)
        return balanceGroup.groupOwnerUserId == account.id
    }

    override fun isExpenseCreator(expenseId: UUID): Boolean {
        val securityContext = SecurityContextHolder.getContext()
        val authentication = securityContext.authentication ?: throw UserNotAuthenticatedException()
        val account = authentication.principal as UserAccount
        val expense = expenseFacade.getById(expenseId)
        return expense.expenseOwnerId == account.id
    }
}