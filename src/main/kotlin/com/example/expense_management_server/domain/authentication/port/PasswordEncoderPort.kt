package com.example.expense_management_server.domain.authentication.port

interface PasswordEncoderPort {
    fun encode(rawPassword: String): String
}