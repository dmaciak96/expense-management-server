package com.example.expense_management_server.adapter.api.expense.model

import com.example.expense_management_server.domain.account.model.Expense
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class ExpenseHttpRequest(

    @Email
    @NotBlank
    @Size(min = 1)
    val paidByEmail: String,

    @NotBlank
    @Size(min = 1)
    val name: String,

    @Positive
    val monetaryAmount: Long
) {
    fun toDomain(createdBy: ApplicationUser, paidBy: ApplicationUser) = Expense(
        id = UUID.randomUUID(),
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        createdBy = createdBy,
        paidBy = paidBy,
        name = name,
        monetaryAmount = monetaryAmount
    )
}