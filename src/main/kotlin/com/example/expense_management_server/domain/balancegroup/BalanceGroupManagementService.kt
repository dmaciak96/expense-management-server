package com.example.expense_management_server.domain.balancegroup

import com.example.expense_management_server.domain.balancegroup.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balancegroup.model.BalanceGroupDomainModel
import com.example.expense_management_server.domain.balancegroup.port.IBalanceGroupPersistencePort
import com.example.expense_management_server.domain.facade.IBalanceGroupManagementFacade
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BalanceGroupManagementService(
    private val balanceGroupPersistencePort: IBalanceGroupPersistencePort,
    private val balanceGroupValidator: BalanceGroupValidator,
    private val userPersistencePort: IUserPersistencePort
) : IBalanceGroupManagementFacade {

    override fun save(balanceGroupDomainModel: BalanceGroupDomainModel): BalanceGroupDomainModel {
        LOGGER.info { "Saving new balance group" }
        balanceGroupValidator.validate(balanceGroupDomainModel)
        return balanceGroupPersistencePort.save(balanceGroupData = balanceGroupDomainModel)
    }

    override fun update(
        balanceGroupId: UUID,
        balanceGroupDomainModel: BalanceGroupDomainModel
    ): BalanceGroupDomainModel {
        LOGGER.info { "Updating balance group with id $balanceGroupId" }
        balanceGroupValidator.validateForUpdate(
            balanceGroupId = balanceGroupId,
            balanceGroupUpdateData = balanceGroupDomainModel
        )
        return balanceGroupPersistencePort.update(
            groupId = balanceGroupId,
            balanceGroupUpdatedData = balanceGroupDomainModel
        )
    }

    override fun delete(balanceGroupId: UUID) {
        LOGGER.info { "Deleting balance group with id $balanceGroupId" }
        balanceGroupValidator.getIfBalanceGroupExists(balanceGroupId)
        balanceGroupPersistencePort.delete(balanceGroupId)
    }

    override fun getById(balanceGroupId: UUID): BalanceGroupDomainModel {
        LOGGER.info { "Fetching balance group with id $balanceGroupId" }
        return balanceGroupValidator.getIfBalanceGroupExists(balanceGroupId)
    }

    override fun getAllWhereUserIsGroupMember(userId: UUID): List<BalanceGroupDomainModel> {
        userPersistencePort.findUserAccountById(userId)
            ?: throw BalanceGroupValidationException("User with id $userId does not exist")
        LOGGER.info { "Fetching balance groups where user $userId is a member" }
        return balanceGroupPersistencePort.getAllWhereUserIsGroupMember(userId)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}