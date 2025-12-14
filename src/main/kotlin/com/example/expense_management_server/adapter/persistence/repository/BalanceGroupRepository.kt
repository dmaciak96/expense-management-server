package com.example.expense_management_server.adapter.persistence.repository

import com.example.expense_management_server.adapter.persistence.model.BalanceGroupEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BalanceGroupRepository : JpaRepository<BalanceGroupEntity, UUID> {
    fun findAllByGroupMembersId(userId: UUID): List<BalanceGroupEntity>
}