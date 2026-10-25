package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.api.auth.AuthenticationController
import com.example.expense_management_server.adapter.api.auth.AuthenticationControllerAdvice
import com.example.expense_management_server.application.application_user.UserAccountVerificationUseCase
import com.example.expense_management_server.application.application_user.UserRegistrationUseCase
import com.example.expense_management_server.application.authentication.GenerateRefreshTokenUseCase
import com.example.expense_management_server.application.authentication.RefreshAccessTokenUseCase
import com.example.expense_management_server.application.authentication.UserLoginUseCase
import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.authentication.model.AuthenticationToken
import com.example.expense_management_server.domain.authentication.model.RefreshToken
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant
import java.util.*

class AuthenticationControllerTest {

    @Mock
    private lateinit var userLoginUseCase: UserLoginUseCase

    @Mock
    private lateinit var userRegistrationUseCase: UserRegistrationUseCase

    @Mock
    private lateinit var userAccountVerificationUseCase: UserAccountVerificationUseCase

    @Mock
    private lateinit var generateRefreshTokenUseCase: GenerateRefreshTokenUseCase

    @Mock
    private lateinit var refreshAccessTokenUseCase: RefreshAccessTokenUseCase

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val controller = AuthenticationController(
            userLoginUseCase = userLoginUseCase,
            userRegistrationUseCase = userRegistrationUseCase,
            userAccountVerificationUseCase = userAccountVerificationUseCase,
            generateRefreshTokenUseCase = generateRefreshTokenUseCase,
            refreshAccessTokenUseCase = refreshAccessTokenUseCase,
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(AuthenticationControllerAdvice())
            .build()
    }

    @Test
    fun `should register user`() {
        // given
        whenever(userRegistrationUseCase.execute(any()))
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        val body = """
            {
              "firstName": "${TestConstants.APPLICATION_USER_ONE.firstName}",
              "lastName": "${TestConstants.APPLICATION_USER_ONE.lastName}",
              "email": "${TestConstants.USER_ONE_EMAIL}",
              "password": "Password1!",
              "phoneNumber": null,
              "displayName": null,
              "avatarUrl": null
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(TestConstants.USER_ONE_ID.toString()) }
            jsonPath("$.email") { value(TestConstants.USER_ONE_EMAIL) }
        }

        verify(userRegistrationUseCase).execute(any())
    }

    @Test
    fun `should return bad request when registration email is invalid`() {
        // given
        val body = """
            {
              "firstName": "Jon",
              "lastName": "Snow",
              "email": "invalid-email",
              "password": "Password1!",
              "phoneNumber": null,
              "displayName": null,
              "avatarUrl": null
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should return conflict error response when user already exists`() {
        // given
        whenever(userRegistrationUseCase.execute(any()))
            .thenThrow(UserAlreadyExistsException("User already exists"))

        val body = """
            {
              "firstName": "Jon",
              "lastName": "Snow",
              "email": "jon.snow@example.com",
              "password": "Password1!",
              "phoneNumber": null,
              "displayName": null,
              "avatarUrl": null
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isConflict() }
            jsonPath("$.message") { value("User already exists") }
            jsonPath("$.status") { value(409) }
        }
    }

    @Test
    fun `should login user`() {
        // given
        val refreshToken = UUID.randomUUID()
        whenever(userLoginUseCase.execute(any()))
            .thenReturn(AuthenticationToken("jwt-token", "dummy-email@test.com"))

        whenever(generateRefreshTokenUseCase.execute(any<String>()))
            .thenReturn(
                RefreshToken(
                    id = UUID.randomUUID(),
                    createdAt = Instant.now(),
                    expireAt = Instant.now(),
                    applicationUserId = UUID.randomUUID(),
                    token = refreshToken
                )
            )

        val body = """
            {
              "email": "jon.snow@example.com",
              "password": "Password1!"
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("jwt-token") }
            jsonPath("$.refreshToken") { value(refreshToken.toString()) }
        }

        verify(userLoginUseCase).execute(any())
    }

    @Test
    fun `should return bad request when email is blank`() {
        // given
        val body = """
            {
              "email": "",
              "password": "Password1!"
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should return unauthorized error response for invalid credentials`() {
        // given
        whenever(userLoginUseCase.execute(any()))
            .thenThrow(BadCredentialsException("Bad credentials"))

        val body = """
            {
              "email": "jon.snow@example.com",
              "password": "WrongPassword"
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Bad credentials") }
            jsonPath("$.status") { value(401) }
        }
    }
}
