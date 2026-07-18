package com.example.expense_management_server.adapter.api.application_user.model

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.Instant
import java.util.*

data class ApplicationUserRegistrationHttpRequest(

    @NotBlank
    val firstName: String,

    @NotBlank
    val lastName: String,

    @Email
    @NotBlank
    val email: String,

    @NotBlank
    @Min(6)
    val password: String,

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
        email = email,
        phoneNumber = phoneNumber,
        password = password,
        displayName = displayName,
        avatarUrl = avatarUrl,
        status = ApplicationUserStatus.ACTIVE
    )
}
