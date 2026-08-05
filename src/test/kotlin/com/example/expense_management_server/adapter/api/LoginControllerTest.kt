package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.adapter.api.login.LoginController
import com.example.expense_management_server.adapter.api.login.LoginControllerAdvice
import com.example.expense_management_server.adapter.security.config.JwtProperties
import com.example.expense_management_server.application.application_user.UserLoginUseCase
import com.example.expense_management_server.domain.authentication.model.AuthenticationToken
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

class LoginControllerTest {

    @Mock
    private lateinit var userLoginUseCase: UserLoginUseCase

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val controller = LoginController(
            userLoginUseCase = userLoginUseCase,
            jwtProperties = JwtProperties(
                secret = "test-secret",
                expiration = 3600
            )
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(LoginControllerAdvice())
            .build()
    }

    @Test
    fun `should login user`() {
        // given
        whenever(userLoginUseCase.execute(any()))
            .thenReturn(AuthenticationToken("jwt-token"))

        val body = """
            {
              "email": "jon.snow@example.com",
              "password": "Password1!"
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("jwt-token") }
            jsonPath("$.tokenType") { value("Bearer") }
            jsonPath("$.expiresIn") { value(3600) }
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
        mockMvc.post("/login") {
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
        mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.message") { value("Bad credentials") }
            jsonPath("$.status") { value(401) }
        }
    }
}
