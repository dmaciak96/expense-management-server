package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.BalanceGroupEntity
import com.example.expense_management_server.adapter.persistence.repository.BalanceGroupRepository
import com.example.expense_management_server.adapter.persistence.repository.ExpenseRepository
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.balancegroup.exception.BalanceGroupNotFoundException
import com.example.expense_management_server.domain.balancegroup.model.BalanceGroupDomainModel
import com.example.expense_management_server.domain.balancegroup.port.IBalanceGroupPersistencePort
import com.example.expense_management_server.domain.expense.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class BalanceGroupPersistenceAdapter(
    private val balanceGroupRepository: BalanceGroupRepository,
    private val userRepository: UserRepository,
    private val expenseRepository: ExpenseRepository,
) : IBalanceGroupPersistencePort {

    override fun save(balanceGroupData: BalanceGroupDomainModel): BalanceGroupDomainModel {
        val savedBalanceGroup = balanceGroupRepository.save(map(balanceGroupData))
        LOGGER.info { "New balance group ${savedBalanceGroup.id} was successfully saved in database" }
        return map(savedBalanceGroup)
    }

    override fun update(
        groupId: UUID,
        balanceGroupUpdatedData: BalanceGroupDomainModel
    ): BalanceGroupDomainModel {
        val currentData = balanceGroupRepository.findById(groupId)
            .orElseThrow { BalanceGroupNotFoundException(groupId) }
        val balanceGroupToUpdate = BalanceGroupEntity(
            id = groupId,
            version = currentData.version,
            createdById = currentData.createdById,
            createdAt = currentData.createdAt,
            updatedAt = OffsetDateTime.now(),
            groupName = balanceGroupUpdatedData.groupName,
            groupMembers = (balanceGroupUpdatedData.groupMemberIds + balanceGroupUpdatedData.groupOwnerUserId)
                .map {
                    userRepository.findById(it)
                        .orElseThrow { UserNotFoundException() }
                }
                .toSet(),
            expenses = currentData.expenses,
        )
        val savedBalanceGroup = balanceGroupRepository.saveAndFlush(balanceGroupToUpdate)
        LOGGER.info { "Balance group ${savedBalanceGroup.id} was successfully updated" }
        return map(savedBalanceGroup)
    }

    override fun getById(groupId: UUID): BalanceGroupDomainModel? {
        return balanceGroupRepository.findById(groupId)
            .map { map(it) }
            .getOrNull()
    }

    override fun getAll(): List<BalanceGroupDomainModel> {
        return balanceGroupRepository.findAll()
            .map { map(it) }
    }

    override fun getAllWhereUserIsGroupMember(userMemberId: UUID): List<BalanceGroupDomainModel> {
        return balanceGroupRepository.findAllByGroupMembersId(userMemberId)
            .map { map(it) }
    }

    override fun delete(groupId: UUID) {
        balanceGroupRepository.deleteById(groupId)
        LOGGER.info { "Balance group $groupId was removed" }
    }

    private fun map(balanceGroupDomainModel: BalanceGroupDomainModel, version: Int? = null): BalanceGroupEntity =
        BalanceGroupEntity(
            id = balanceGroupDomainModel.id,
            version = version,
            createdAt = balanceGroupDomainModel.createdAt,
            updatedAt = balanceGroupDomainModel.updatedAt,
            groupName = balanceGroupDomainModel.groupName,
            groupMembers = (balanceGroupDomainModel.groupMemberIds + balanceGroupDomainModel.groupOwnerUserId)
                .map {
                    userRepository.findById(it)
                        .orElseThrow { UserNotFoundException() }
                }
                .toSet(),
            expenses = balanceGroupDomainModel.expenseIds
                .map {
                    expenseRepository.findById(it)
                        .orElseThrow { ExpenseNotFoundException(UUID.randomUUID()) }
                }
        )

    private fun map(balanceGroupEntity: BalanceGroupEntity): BalanceGroupDomainModel =
        BalanceGroupDomainModel(
            id = balanceGroupEntity.id,
            groupName = balanceGroupEntity.groupName,
            groupMemberIds = balanceGroupEntity.groupMembers
                .map { it.id!! },
            expenseIds = balanceGroupEntity.expenses
                .map { it.id!! },
            groupOwnerUserId = balanceGroupEntity.createdById!!,
            createdAt = balanceGroupEntity.createdAt,
            updatedAt = balanceGroupEntity.updatedAt
        )

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}