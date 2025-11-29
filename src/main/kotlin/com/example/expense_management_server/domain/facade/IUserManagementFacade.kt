package com.example.expense_management_server.domain.facade

import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRegistrationDomainModel
import java.util.UUID

interface IUserManagementFacade {
    fun registerNewUser(userRegistrationModel: UserRegistrationDomainModel): UserDomainModel
    fun getUserById(id: UUID): UserDomainModel
}