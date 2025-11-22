package com.example.expense_management_server.domain.user.port

import com.example.expense_management_server.domain.user.model.UserDomainModel

interface IUserPersistencePort {
    fun saveUserAccount(userModel: UserDomainModel): UserDomainModel
    fun findUserAccountByEmail(email: String): UserDomainModel?
}