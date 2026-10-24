package com.example.expense_management_server.adapter.api.auth.model

data class LoginHttpResponse(
    val accessToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
)
