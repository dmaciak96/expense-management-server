package com.example.expense_management_server.adapter.persistence.repository

import com.example.expense_management_server.adapter.persistence.model.ApplicationUserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<ApplicationUserEntity, UUID> {
    fun findByEmail(email: String): ApplicationUserEntity?
}