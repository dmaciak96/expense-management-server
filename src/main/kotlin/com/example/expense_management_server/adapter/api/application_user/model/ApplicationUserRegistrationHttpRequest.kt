package com.example.expense_management_server.adapter.api.application_user.model

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class ApplicationUserRegistrationHttpRequest(

    @NotBlank
    @Size(min = 2)
    val firstName: String,

    @NotBlank
    @Size(min = 2)
    val lastName: String,

    @Email
    @NotBlank
    val email: String,

    @NotBlank
    @Size(min = 6)
    val password: String,

    val phoneNumber: String?,
    val displayName: String?,
    val avatarUrl: String?
) {
    fun toDomain() = ApplicationUser(
        id = UUID.randomUUID(),
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        firstName = firstName.trim(),
        lastName = lastName.trim(),
        email = email.trim(),
        phoneNumber = phoneNumber?.trim(),
        password = password.trim(),
        displayName = displayName?.trim(),
        avatarUrl = avatarUrl?.trim(),
        status = ApplicationUserStatus.ACTIVE
    )
}
