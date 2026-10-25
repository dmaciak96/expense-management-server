package com.example.expense_management_server.domain.authentication.port

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.authentication.model.AuthenticationToken
import com.example.expense_management_server.domain.authentication.model.UserAuthentication

interface AuthenticationPort {
    fun generateAuthenticationToken(userAuthentication: UserAuthentication): AuthenticationToken
    fun generateAccountVerificationToken(applicationUser: ApplicationUser): String
    fun validateVerificationTokenAndGetEmail(token: String): String
}