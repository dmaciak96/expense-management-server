package com.example.expense_management_server.domain.user.port

interface IEmailVerificationPort {
    fun sendEmailVerificationMessage(email: String)
}