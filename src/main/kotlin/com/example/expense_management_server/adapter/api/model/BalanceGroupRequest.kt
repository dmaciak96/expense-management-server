package com.example.expense_management_server.adapter.api.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

data class BalanceGroupRequest(

    @NotBlank
    @Size(max = 128, message = "Name cannot be empty")
    val groupName: String,
    val groupMemberIds: List<UUID>,
)
