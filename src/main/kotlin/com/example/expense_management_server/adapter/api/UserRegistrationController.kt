package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.domain.user.registration.IUserRegistrationFacade
import org.springframework.web.bind.annotation.RestController

@RestController
class UserRegistrationController(
    private val userRegistrationFacade: IUserRegistrationFacade
) {
    // TODO: Implement registration API
}