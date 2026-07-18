package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import org.springframework.stereotype.Component

@Component
class ApplicationUserRegistrationUseCase {

    fun execute(applicationUser: ApplicationUser): ApplicationUser {
        throw NotImplementedError()
    }
}
