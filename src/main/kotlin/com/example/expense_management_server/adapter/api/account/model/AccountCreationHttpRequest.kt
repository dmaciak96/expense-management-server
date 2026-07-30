package com.example.expense_management_server.adapter.api.account.model

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class AccountCreationHttpRequest(

    @NotBlank
    @Size(min = 1, max = 64)
    val name: String,

    @NotBlank
    val currency: Currency,
) {
    fun toDomain() = Account(
        id = UUID.randomUUID(),
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        createdBy = null,
        name = this.name,
        currency = this.currency,
        members = emptySet(),
        expenses = emptySet(),
        status = AccountStatus.ACTIVE,
    )
}
