package com.example.expense_management_server.domain.user.port

interface IPasswordEncoderPort {
    fun encodePassword(password: String): String
}