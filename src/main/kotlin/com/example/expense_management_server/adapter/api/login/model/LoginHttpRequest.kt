package com.example.expense_management_server.adapter.api.login.model

import jakarta.validation.constraints.NotBlank

data class LoginHttpRequest(
    @NotBlank
    val email: String,

    @NotBlank
    val password: String
)
