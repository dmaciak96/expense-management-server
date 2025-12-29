package com.example.expense_management_server.domain.user.port

import com.example.expense_management_server.domain.user.model.UserModel
import java.util.UUID

interface IUserPersistencePort {
    fun saveOrUpdateUserAccount(userModel: UserModel): UserModel
    fun findUserAccountByEmail(email: String): UserModel?
    fun findUserAccountById(id: UUID): UserModel?
    fun deleteUser(userModel: UserModel)
}