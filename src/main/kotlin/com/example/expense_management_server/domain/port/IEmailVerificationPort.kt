package com.example.expense_management_server.domain.port

interface IEmailVerificationPort {
    fun sendEmailVerificationMessage(email: String)
}