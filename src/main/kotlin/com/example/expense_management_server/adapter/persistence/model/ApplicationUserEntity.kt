package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document
data class ApplicationUserEntity(
    @Id val id: String,
    @CreatedDate val createdAt: Instant,
    @LastModifiedDate val lastUpdatedAt: Instant,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val password: String,
    val displayName: String?,
    val avatarUrl: String?,
    val status: ApplicationUserStatus,
) {
    fun toDomain() = ApplicationUser(
        id = UUID.fromString(this.id),
        createdAt = this.createdAt,
        lastUpdatedAt = this.lastUpdatedAt,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phoneNumber = this.phoneNumber,
        password = this.password,
        displayName = this.displayName,
        avatarUrl = this.avatarUrl,
        status = this.status
    )

    companion object {
        fun fromDomain(domain: ApplicationUser) = ApplicationUserEntity(
            id = domain.id.toString(),
            createdAt = domain.createdAt,
            lastUpdatedAt = domain.lastUpdatedAt,
            firstName = domain.firstName,
            lastName = domain.lastName,
            email = domain.email,
            phoneNumber = domain.phoneNumber,
            password = domain.password,
            displayName = domain.displayName,
            avatarUrl = domain.avatarUrl,
            status = domain.status
        )
    }
}