package com.example.expense_management_server.domain.port

import com.example.expense_management_server.domain.user.model.UserDomainModel
import java.util.UUID

interface IUserPersistencePort {
    fun saveUserAccount(userModel: UserDomainModel): UserDomainModel
    fun findUserAccountByEmail(email: String): UserDomainModel?
    fun findUserAccountById(id: UUID): UserDomainModel?
}