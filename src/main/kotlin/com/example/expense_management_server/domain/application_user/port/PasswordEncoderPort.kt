package com.example.expense_management_server.domain.application_user.port

interface PasswordEncoderPort {
    fun encode(rawPassword: String): String
}