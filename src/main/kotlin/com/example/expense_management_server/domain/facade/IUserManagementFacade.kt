package com.example.expense_management_server.domain.facade

import com.example.expense_management_server.domain.user.model.UserHttpModel
import com.example.expense_management_server.domain.user.model.UserModel
import java.util.UUID

interface IUserManagementFacade {
    fun registerNewUser(userHttpModel: UserHttpModel): UserModel
    fun getUserById(id: UUID): UserModel
    fun getUserByEmail(email: String): UserModel
    fun updateUser(userId: UUID, userHttpModel: UserHttpModel): UserModel
    fun deleteUser(userId: UUID)
}