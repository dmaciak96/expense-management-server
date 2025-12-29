package com.example.expense_management_server.domain.balance

import com.example.expense_management_server.domain.balance.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balance.model.BalanceGroup
import com.example.expense_management_server.domain.balance.port.IBalanceGroupPersistencePort
import com.example.expense_management_server.domain.facade.IBalanceManagementFacade
import com.example.expense_management_server.domain.facade.IExpenseManagementFacade
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BalanceManagementService(
    private val balanceGroupPersistencePort: IBalanceGroupPersistencePort,
    private val balanceGroupValidator: BalanceGroupValidator,
    private val userPersistencePort: IUserPersistencePort,
    private val expenseManagementFacade: IExpenseManagementFacade
) : IBalanceManagementFacade {

    override fun save(balanceGroup: BalanceGroup): BalanceGroup {
        LOGGER.info { "Saving new balance group" }
        balanceGroupValidator.validate(balanceGroup)
        return balanceGroupPersistencePort.save(balanceGroup)
    }

    override fun update(
        balanceGroupId: UUID,
        balanceGroup: BalanceGroup
    ): BalanceGroup {
        LOGGER.info { "Updating balance group with id $balanceGroupId" }
        balanceGroupValidator.validateForUpdate(balanceGroupId, balanceGroup)
        return balanceGroupPersistencePort.update(balanceGroupId, balanceGroup)
    }

    override fun delete(balanceGroupId: UUID) {
        LOGGER.info { "Deleting balance group with id $balanceGroupId" }
        balanceGroupValidator.getIfBalanceGroupExists(balanceGroupId)
        balanceGroupPersistencePort.delete(balanceGroupId)
    }

    override fun getById(balanceGroupId: UUID): BalanceGroup {
        LOGGER.info { "Fetching balance group with id $balanceGroupId" }
        return balanceGroupValidator.getIfBalanceGroupExists(balanceGroupId)
    }

    override fun getAllWhereUserIsGroupMember(userId: UUID): List<BalanceGroup> {
        userPersistencePort.findUserAccountById(userId)
            ?: throw BalanceGroupValidationException("User with id $userId does not exist")
        LOGGER.info { "Fetching balance groups where user $userId is a member" }
        return balanceGroupPersistencePort.getAllWhereUserIsGroupMember(userId)
    }

    override fun calculateBalance(balanceGroupId: UUID, userId: UUID): Double {
        try {
            LOGGER.info { "Calculating balance from group $balanceGroupId for user $userId" }
            val balanceGroup = getById(balanceGroupId)

            val membersCount = balanceGroup.groupMemberIds.size
            LOGGER.debug { "Expenses has to be split between $membersCount members" }

            val balanceGroupExpenses = expenseManagementFacade.getAllByBalanceGroup(balanceGroupId)
            LOGGER.debug { "Balance group has ${balanceGroupExpenses.size} expenses" }

            val userExpenses = balanceGroupExpenses
                .filter { it.expenseOwnerId == userId }
            LOGGER.debug { "User $userId has ${userExpenses.size} expenses" }

            val moneySpentByGroup = balanceGroupExpenses.sumOf { it.amount }
            LOGGER.debug { "All group spent: $moneySpentByGroup" }

            val moneyToSpentByEachMember = moneySpentByGroup / membersCount
            LOGGER.debug { "Each group member need to spend: $moneyToSpentByEachMember" }

            val moneySpentByUser = userExpenses.sumOf { it.amount }
            LOGGER.debug { "User spent: $moneySpentByUser" }

            val balance = moneySpentByUser - moneyToSpentByEachMember
            LOGGER.info { "Calculated balance for user $userId is $balance" }
            return balance
        } catch (e: Exception) {
            LOGGER.warn { "Balance calculation failed for user $userId, message: ${e.message}" }
            return 0.0
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}