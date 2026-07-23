package com.example.expense_management_server.adapter.api.application_user.model

import jakarta.validation.constraints.NotBlank

data class ResetPasswordHttpRequest(

    @NotBlank
    val oldPassword: String,

    @NotBlank
    val newPassword: String
)
