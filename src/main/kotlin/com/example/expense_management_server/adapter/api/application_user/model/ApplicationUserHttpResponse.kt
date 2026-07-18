package com.example.expense_management_server.adapter.api.application_user.model

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import java.time.Instant
import java.util.*

data class ApplicationUserHttpResponse(
    val id: UUID,
    val createdAt: Instant,
    val lastUpdatedAt: Instant,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val displayName: String?,
    val avatarUrl: String?,
    val status: ApplicationUserStatus,
) {
    companion object {
        fun fromDomain(domain: ApplicationUser) = ApplicationUserHttpResponse(
            id = domain.id,
            createdAt = domain.createdAt,
            lastUpdatedAt = domain.lastUpdatedAt,
            firstName = domain.firstName,
            lastName = domain.lastName,
            email = domain.email,
            phoneNumber = domain.phoneNumber,
            displayName = domain.displayName,
            avatarUrl = domain.avatarUrl,
            status = domain.status,
        )
    }
}
