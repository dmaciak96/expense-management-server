package com.example.expense_management_server.unit.adapter.api

import com.example.expense_management_server.adapter.api.UserManagementController
import com.example.expense_management_server.adapter.api.model.UserHttpRequest
import com.example.expense_management_server.adapter.api.model.UserResponse
import com.example.expense_management_server.domain.facade.IUserManagementFacade
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserHttpDomainModel
import com.example.expense_management_server.domain.user.model.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserManagementControllerTest {

    @Mock
    private lateinit var userRegistrationFacade: IUserManagementFacade

    private lateinit var userManagementController: UserManagementController

    @BeforeEach
    fun initialize() {
        userManagementController = UserManagementController(
            userManagementFacade = userRegistrationFacade
        )
    }

    @Test
    fun `when sending registration request then register new user via registration facade`() {
        // given
        whenever(userRegistrationFacade.registerNewUser(eq(USER_REGISTRATION_DOMAIN_MODEL)))
            .thenReturn(USER_DOMAIN_MODEL)

        // when
        val result = userManagementController.registerNewUser(USER_REGISTRATION_REQUEST)

        // then
        verify(userRegistrationFacade)
            .registerNewUser(eq(USER_REGISTRATION_DOMAIN_MODEL))

        assertThat(result)
            .isEqualTo(USER_REGISTRATION_RESPONSE)
    }

    @Test
    fun `when asking for user details then get user from facade and map to response`() {
        // given
        whenever(userRegistrationFacade.getUserById(eq(USER_ID)))
            .thenReturn(USER_DOMAIN_MODEL)

        // when
        val result = userManagementController.getUserDetails(USER_ID)

        // then
        verify(userRegistrationFacade).getUserById(eq(USER_ID))

        assertThat(result)
            .isEqualTo(USER_REGISTRATION_RESPONSE)
    }

    @Test
    fun `when updating user account then call facade and map response`() {
        // given
        whenever(
            userRegistrationFacade.updateUser(
                eq(USER_ID),
                eq(USER_REGISTRATION_DOMAIN_MODEL)
            )
        ).thenReturn(USER_DOMAIN_MODEL)

        // when
        val result = userManagementController.updateUserAccount(
            USER_ID,
            USER_REGISTRATION_REQUEST
        )

        // then
        verify(userRegistrationFacade)
            .updateUser(eq(USER_ID), eq(USER_REGISTRATION_DOMAIN_MODEL))

        assertThat(result)
            .isEqualTo(USER_REGISTRATION_RESPONSE)
    }


    companion object {
        private val USER_ID = UUID.randomUUID()
        private val CREATED_AT = OffsetDateTime.parse("2007-12-03T10:15:30+01:00")
        private val PASSWORD_HASH = "password_hash"
        private val PASSWORD = "password"
        private val EMAIL = "test@test.com"
        private val NICKNAME = "testUser"

        private val USER_DOMAIN_MODEL = UserDomainModel(
            id = USER_ID,
            email = EMAIL,
            nickname = NICKNAME,
            passwordHash = PASSWORD_HASH,
            role = UserRole.USER,
            isEmailVerified = false,
            createdAt = CREATED_AT,
            updatedAt = null,
            lastLoginAt = null,
            accountStatus = AccountStatus.ACTIVE
        )

        private val USER_REGISTRATION_DOMAIN_MODEL = UserHttpDomainModel(
            email = EMAIL,
            password = PASSWORD,
            nickname = NICKNAME,
        )

        private val USER_REGISTRATION_REQUEST = UserHttpRequest(
            email = EMAIL,
            password = PASSWORD,
            nickname = NICKNAME,
        )

        private val USER_REGISTRATION_RESPONSE = UserResponse(
            id = USER_ID,
            email = EMAIL,
            nickname = NICKNAME,
            emailVerified = false,
            createdAt = CREATED_AT,
            updatedAt = null,
            lastLoginAt = null,
            accountStatus = AccountStatus.ACTIVE
        )
    }
}