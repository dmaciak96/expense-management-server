package com.example.expense_management_server.adapter.api.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserRequest(

    @Email
    @NotNull
    val email: String,

    @NotNull
    @Size(min = 8, max = 64)
    val password: String,

    val nickname: String?,
)
