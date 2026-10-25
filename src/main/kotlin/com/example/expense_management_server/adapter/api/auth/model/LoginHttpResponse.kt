package com.example.expense_management_server.adapter.api.auth.model

data class LoginHttpResponse(
    val accessToken: String,
    val refreshToken: String,
)
