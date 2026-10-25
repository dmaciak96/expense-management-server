package com.example.expense_management_server.adapter.api.application_user.model

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import jakarta.validation.constraints.NotBlank
import java.time.Instant
import java.util.*

data class ApplicationUserUpdateHttpRequest(
    @NotBlank
    val firstName: String,

    @NotBlank
    val lastName: String,

    val phoneNumber: String?,
    val displayName: String?,
    val avatarUrl: String?
) {
    fun toDomain() = ApplicationUser(
        id = UUID.randomUUID(),
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        firstName = firstName,
        lastName = lastName,
        email = "",
        phoneNumber = phoneNumber,
        password = "",
        displayName = displayName,
        avatarUrl = avatarUrl,
        status = ApplicationUserStatus.ACTIVE
    )
}
