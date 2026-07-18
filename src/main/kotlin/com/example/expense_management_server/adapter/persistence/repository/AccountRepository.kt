package com.example.expense_management_server.adapter.persistence.repository

import com.example.expense_management_server.adapter.persistence.model.AccountEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccountRepository: CrudRepository<AccountEntity, UUID> {
    fun findByName(name: String): AccountEntity?
    fun findAllByCreatedById(userId: UUID): List<AccountEntity>
}