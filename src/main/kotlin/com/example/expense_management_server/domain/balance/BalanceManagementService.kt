package com.example.expense_management_server.domain.balance

import com.example.expense_management_server.domain.balance.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balance.model.BalanceGroup
import com.example.expense_management_server.domain.balance.port.IBalanceGroupPersistencePort
import com.example.expense_management_server.domain.facade.IBalanceManagementFacade
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BalanceManagementService(
    private val balanceGroupPersistencePort: IBalanceGroupPersistencePort,
    private val balanceGroupValidator: BalanceGroupValidator,
    private val userPersistencePort: IUserPersistencePort
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

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}