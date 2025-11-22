package com.example.expense_management_server.domain.user.registration

import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRegistrationDomainModel

interface IUserRegistrationFacade {
    fun registerNewUser(userRegistrationModel: UserRegistrationDomainModel): UserDomainModel
}