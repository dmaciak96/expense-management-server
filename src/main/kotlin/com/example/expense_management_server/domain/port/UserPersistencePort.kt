package com.example.expense_management_server.domain.port

import com.example.expense_management_server.domain.model.ApplicationUser
import java.util.*

interface UserPersistencePort {
    fun create(user: ApplicationUser): ApplicationUser
    fun findByEmail(email: String): ApplicationUser
    fun findById(id: UUID): ApplicationUser
    fun update(user: ApplicationUser): ApplicationUser
    fun deleteById(id: UUID)
}