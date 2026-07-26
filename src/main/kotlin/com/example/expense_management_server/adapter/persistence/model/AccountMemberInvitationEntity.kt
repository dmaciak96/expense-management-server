package com.example.expense_management_server.adapter.persistence.model

import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document
data class AccountMemberInvitationEntity(
    @Id val id: String,
    @CreatedDate val createdAt: Instant,
    @CreatedBy val createdBy: ApplicationUserEntity,
    val accountId: String,
    val email: String,
    val token: String,
    val expiresAt: Instant,
    val acceptedAt: Instant?,
    val status: AccountMemberInvitationStatus
) {
    fun toDomain() = AccountMemberInvitation(
        id = UUID.fromString(this.id),
        createdAt = this.createdAt,
        createdBy = this.createdBy.toDomain(),
        accountId = UUID.fromString(this.accountId),
        email = this.email,
        token = this.token,
        expiresAt = this.expiresAt,
        acceptedAt = this.acceptedAt,
        status = this.status
    )

    companion object {
        fun fromDomain(domain: AccountMemberInvitation) = AccountMemberInvitationEntity(
            id = domain.id.toString(),
            createdAt = domain.createdAt,
            createdBy = ApplicationUserEntity.fromDomain(domain.createdBy),
            accountId = domain.accountId.toString(),
            email = domain.email,
            token = domain.token,
            expiresAt = domain.expiresAt,
            acceptedAt = domain.acceptedAt,
            status = domain.status
        )
    }
}
