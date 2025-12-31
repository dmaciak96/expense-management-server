package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.BalanceGroupEntity
import com.example.expense_management_server.adapter.persistence.repository.BalanceGroupRepository
import com.example.expense_management_server.adapter.persistence.repository.ExpenseRepository
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.balance.exception.BalanceGroupNotFoundException
import com.example.expense_management_server.domain.balance.model.BalanceGroup
import com.example.expense_management_server.domain.balance.port.BalanceGroupPersistencePort
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
) : BalanceGroupPersistencePort {

    override fun save(balanceGroup: BalanceGroup): BalanceGroup {
        val balanceGroupEntity = balanceGroupRepository.save(mapToModel(balanceGroup))
        LOGGER.info { "New balance group ${balanceGroupEntity.id} was successfully saved in database" }
        return mapToModel(balanceGroupEntity)
    }

    override fun update(
        balanceGroupId: UUID,
        balanceGroup: BalanceGroup
    ): BalanceGroup {
        val targetEntity = balanceGroupRepository.findById(balanceGroupId)
            .orElseThrow { BalanceGroupNotFoundException(balanceGroupId) }

        val sourceEntity = BalanceGroupEntity(
            id = balanceGroupId,
            version = targetEntity.version,
            createdById = targetEntity.createdById,
            createdAt = targetEntity.createdAt,
            updatedAt = OffsetDateTime.now(),
            groupName = balanceGroup.groupName,
            groupMembers = (balanceGroup.groupMemberIds + balanceGroup.groupOwnerUserId)
                .map {
                    userRepository.findById(it)
                        .orElseThrow { UserNotFoundException() }
                }
                .toSet(),
            expenses = targetEntity.expenses,
        )
        val savedEntity = balanceGroupRepository.saveAndFlush(sourceEntity)
        LOGGER.info { "Balance group ${savedEntity.id} was successfully updated" }
        return mapToModel(savedEntity)
    }

    override fun getById(balanceGroupId: UUID): BalanceGroup? {
        return balanceGroupRepository.findById(balanceGroupId)
            .map { mapToModel(it) }
            .getOrNull()
    }

    override fun getAll(): List<BalanceGroup> {
        return balanceGroupRepository.findAll()
            .map { mapToModel(it) }
    }

    override fun getAllWhereUserIsGroupMember(balanceGroupMemberId: UUID): List<BalanceGroup> {
        return balanceGroupRepository.findAllByGroupMembersId(balanceGroupMemberId)
            .map { mapToModel(it) }
    }

    override fun delete(balanceGroupId: UUID) {
        balanceGroupRepository.deleteById(balanceGroupId)
        LOGGER.info { "Balance group $balanceGroupId was removed" }
    }

    private fun mapToModel(balanceGroup: BalanceGroup, version: Int? = null): BalanceGroupEntity =
        BalanceGroupEntity(
            id = balanceGroup.id,
            version = version,
            createdAt = balanceGroup.createdAt,
            updatedAt = balanceGroup.updatedAt,
            groupName = balanceGroup.groupName,
            groupMembers = (balanceGroup.groupMemberIds + balanceGroup.groupOwnerUserId)
                .map {
                    userRepository.findById(it)
                        .orElseThrow { UserNotFoundException() }
                }
                .toSet(),
            expenses = balanceGroup.expenseIds
                .map {
                    expenseRepository.findById(it)
                        .orElseThrow { ExpenseNotFoundException(UUID.randomUUID()) }
                }
        )

    private fun mapToModel(balanceGroupEntity: BalanceGroupEntity): BalanceGroup =
        BalanceGroup(
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