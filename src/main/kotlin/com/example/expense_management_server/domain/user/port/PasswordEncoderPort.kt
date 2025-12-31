package com.example.expense_management_server.domain.user.port

interface PasswordEncoderPort {
    fun encodePassword(password: String): String
}