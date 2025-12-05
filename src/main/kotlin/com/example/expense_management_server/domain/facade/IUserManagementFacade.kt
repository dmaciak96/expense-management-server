package com.example.expense_management_server.domain.facade

import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserHttpDomainModel
import java.util.UUID

interface IUserManagementFacade {
    fun registerNewUser(userRegistrationModel: UserHttpDomainModel): UserDomainModel
    fun getUserById(id: UUID): UserDomainModel
    fun getUserByEmail(email: String): UserDomainModel
    fun updateUser(userId: UUID, userUpdateModel: UserHttpDomainModel): UserDomainModel
}