package com.example.expense_management_server.adapter.api.account.model

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class AccountUpdateHttpRequest(
    @NotBlank
    @Size(min = 1, max = 64)
    val name: String,

    val currency: Currency,

    @NotNull
    val status: AccountStatus
) {
    fun toDomain(id: UUID, createdBy: ApplicationUser) = Account(
        id = id,
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        createdBy = createdBy,
        name = this.name,
        currency = this.currency,
        members = emptySet(),
        expenses = emptySet(),
        status = this.status
    )
}
