package com.example.expense_management_server.domain.user.port

interface UserAuthenticationPort {
    fun authenticateAndGenerateJwtToken(email: String, password: String): String
}