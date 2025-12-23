package com.example.expense_management_server.adapter.api.model

import com.example.expense_management_server.domain.expense.model.ExpenseSplitType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ExpenseRequest(

    @NotBlank
    @Size(max = 128, message = "Name cannot be empty")
    val name: String,

    @Positive
    val amount: Double,
    val splitType: ExpenseSplitType,
)
