package com.example.expense_management_server.domain.port

interface IPasswordEncoderPort {
    fun encodePassword(password: String): String
}