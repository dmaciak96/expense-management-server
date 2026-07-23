package com.example.expense_management_server.domain.application_user.port

import com.example.expense_management_server.domain.application_user.model.ApplicationUser

interface UserValidationPort {
    fun validate(user: ApplicationUser)
}