package com.example.expense_management_server.domain.balance

import com.example.expense_management_server.domain.balance.exception.BalanceGroupNotFoundException
import com.example.expense_management_server.domain.balance.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balance.model.BalanceGroup
import com.example.expense_management_server.domain.balance.port.IBalanceGroupPersistencePort
import com.example.expense_management_server.domain.expense.port.IExpensePersistencePort
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class BalanceGroupValidator(
    private val balanceGroupPersistencePort: IBalanceGroupPersistencePort,
    private val expensePersistencePort: IExpensePersistencePort,
    private val userPersistencePort: IUserPersistencePort
) {

    fun validate(balanceGroup: BalanceGroup) {
        checkIfOwnerExists(balanceGroup.groupOwnerUserId)
        checkIfNameIsBlank(balanceGroup.groupName)
        checkIfAllMembersExists(balanceGroup.groupMemberIds)
        checkIfAllExpensesExists(balanceGroup.expenseIds)
    }

    fun validateForUpdate(balanceGroupId: UUID, balanceGroup: BalanceGroup) {
        validate(balanceGroup)
        getIfBalanceGroupExists(balanceGroupId)
    }

    fun getIfBalanceGroupExists(balanceGroupId: UUID): BalanceGroup {
        val balanceGroup =
            balanceGroupPersistencePort.getById(balanceGroupId) ?: throw BalanceGroupNotFoundException(balanceGroupId)
        return balanceGroup
    }

    private fun checkIfAllMembersExists(balanceGroupMemberIds: List<UUID>) {
        val notExistingUsers = balanceGroupMemberIds
            .filter { userPersistencePort.findUserAccountById(it) == null }
        if (notExistingUsers.isNotEmpty()) {
            LOGGER.debug { "Users not found in database $notExistingUsers" }
            throw BalanceGroupValidationException("Some of the members have not been found")
        }
    }

    private fun checkIfAllExpensesExists(expensesIds: List<UUID>) {
        val notExistingExpenses = expensesIds.filter { expensePersistencePort.getById(it) == null }
        if (notExistingExpenses.isNotEmpty()) {
            LOGGER.debug { "Expenses not found in database $notExistingExpenses" }
            throw BalanceGroupValidationException("Some of the expenses have not been found")
        }
    }

    private fun checkIfNameIsBlank(name: String) {
        if (name.isBlank()) {
            LOGGER.debug { "Balance group name ($name) is blank, balance group cannot be created" }
            throw BalanceGroupValidationException("Balance group name cannot contains whitespaces only")
        }
        LOGGER.debug { "Balance group name ($name) is valid" }
    }

    private fun checkIfOwnerExists(ownerId: UUID) {
        val owner = userPersistencePort.findUserAccountById(ownerId)
        if (owner == null) {
            LOGGER.debug { "Balance group ($ownerId) not found" }
            throw BalanceGroupValidationException("Balance group owner does not exist")
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}