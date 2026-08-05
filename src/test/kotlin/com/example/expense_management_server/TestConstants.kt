package com.example.expense_management_server

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
import com.example.expense_management_server.domain.account.model.Expense
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import java.time.Instant
import java.util.*

object TestConstants {
    val USER_ONE_ID = UUID.fromString("1041ff4c-7870-4fd0-aa10-e8189bb8dde3")
    val USER_ONE_FIRST_NAME = "Jon"
    val USER_ONE_LAST_NAME = "Snow"
    val USER_ONE_EMAIL = "jon.snow@night-watch.com"
    val USER_ONE_PASSWORD = "WinterIsC0mming"
    val USER_ONE_PHONE = "123-123-123"
    val USER_ONE_DISPLAY_NAME = "Lord Commander"
    val USER_ONE_AVATAR_URL = "https://png.com/ghost.png"
    val APPLICATION_USER_ONE = ApplicationUser(
        id = USER_ONE_ID,
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        firstName = USER_ONE_FIRST_NAME,
        lastName = USER_ONE_LAST_NAME,
        email = USER_ONE_EMAIL,
        password = USER_ONE_PASSWORD,
        phoneNumber = USER_ONE_PHONE,
        displayName = USER_ONE_DISPLAY_NAME,
        avatarUrl = USER_ONE_AVATAR_URL,
        status = ApplicationUserStatus.ACTIVE,
    )

    val USER_TWO_ID = UUID.fromString("fc927e87-e3f7-413b-807b-7c57508169b5")
    val USER_TWO_FIRST_NAME = "Sansa"
    val USER_TWO_LAST_NAME = "Stark"
    val USER_TWO_EMAIL = "sansa.stark@winterfell.com"
    val USER_TWO_PASSWORD = "F***Joffrey"
    val USER_TWO_PHONE = "321-123-321"
    val USER_TWO_DISPLAY_NAME = "Queen of The North"
    val USER_TWO_AVATAR_URL = "https://png.com/lady.png"
    val APPLICATION_USER_TWO = ApplicationUser(
        id = USER_TWO_ID,
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        firstName = USER_TWO_FIRST_NAME,
        lastName = USER_TWO_LAST_NAME,
        email = USER_TWO_EMAIL,
        password = USER_TWO_PASSWORD,
        phoneNumber = USER_TWO_PHONE,
        displayName = USER_TWO_DISPLAY_NAME,
        avatarUrl = USER_TWO_AVATAR_URL,
        status = ApplicationUserStatus.ACTIVE,
    )

    val EXPENSE_ID = UUID.fromString("d857e48f-8a03-4e98-a366-dc203380f28a")
    val EXPENSE_NAME = "Wall reparation"
    val EXPENSE_MONETARY_AMAOUNT = 100_000_000L
    val EXPENSE = Expense(
        id = EXPENSE_ID,
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        createdBy = APPLICATION_USER_ONE,
        paidBy = APPLICATION_USER_ONE,
        name = EXPENSE_NAME,
        monetaryAmount = EXPENSE_MONETARY_AMAOUNT
    )

    val ACCOUNT_ID = UUID.fromString("c9dbd8dc-b8bb-4b43-b663-4773a3bb0b5f")
    val ACCOUNT_NAME = "The North Budget"
    val ACCOUNT_CURRENCY = Currency.USD
    val ACCOUNT = Account(
        id = ACCOUNT_ID,
        createdAt = Instant.now(),
        lastUpdatedAt = Instant.now(),
        createdBy = APPLICATION_USER_ONE,
        name = ACCOUNT_NAME,
        currency = ACCOUNT_CURRENCY,
        members = setOf(APPLICATION_USER_ONE.toAccountMember(), APPLICATION_USER_TWO.toAccountMember()),
        expenses = setOf(EXPENSE),
        status = AccountStatus.ACTIVE,
    )

    val INVITATION_ID = UUID.fromString("1783c509-022e-4abf-83d1-76fb4e64c8f5")
    val INVITATION_TOKEN = "test-invitation-token"
    val INVITATION_EXPIRES_AT = Instant.now()
    val INVITATION_ACCEPTED_AT = null
    val INVITATION_STATUS = AccountMemberInvitationStatus.PENDING
    val INVITATION = AccountMemberInvitation(
        id = INVITATION_ID,
        createdAt = Instant.now(),
        createdBy = APPLICATION_USER_ONE,
        accountId = ACCOUNT.id,
        email = APPLICATION_USER_TWO.email,
        token = INVITATION_TOKEN,
        expiresAt = INVITATION_EXPIRES_AT,
        acceptedAt = INVITATION_ACCEPTED_AT,
        status = INVITATION_STATUS
    )
}