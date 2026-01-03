package com.example.expense_management_server.adapter.api.model

data class AuthenticationRequest(
    val email: String,
    val password: String,
)
