package com.example.expense_management_server.domain.service

import com.example.expense_management_server.domain.user.model.UserAuthenticationModel

interface UserAuthenticationService {
    fun authenticate(email: String, password: String): UserAuthenticationModel
}