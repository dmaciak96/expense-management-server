package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.api.application_user.ApplicationUserController
import com.example.expense_management_server.adapter.api.application_user.ApplicationUserControllerAdvice
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.application.application_user.RemoveCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.UpdateUserDataUseCase
import com.example.expense_management_server.application.application_user.UserRegistrationUseCase
import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ApplicationUserControllerTest {

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    @Mock
    private lateinit var fetchUserByEmailUseCase: FetchUserByEmailUseCase

    @Mock
    private lateinit var userRegistrationUseCase: UserRegistrationUseCase

    @Mock
    private lateinit var removeCurrentLoginUserUseCase: RemoveCurrentLoginUserUseCase

    @Mock
    private lateinit var updateUserDataUseCase: UpdateUserDataUseCase

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val controller = ApplicationUserController(
            fetchCurrentLoginUserUseCase,
            fetchUserByEmailUseCase,
            userRegistrationUseCase,
            removeCurrentLoginUserUseCase,
            updateUserDataUseCase
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(ApplicationUserControllerAdvice())
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
        mockMvc.post("/users") {
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
        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should return current logged user`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        // when & then
        mockMvc.get("/users")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(TestConstants.USER_ONE_ID.toString()) }
                jsonPath("$.email") { value(TestConstants.USER_ONE_EMAIL) }
            }

        verify(fetchCurrentLoginUserUseCase).execute()
    }

    @Test
    fun `should return user by email`() {
        // given
        whenever(fetchUserByEmailUseCase.execute(TestConstants.USER_ONE_EMAIL))
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        // when & then
        mockMvc.get("/users/${TestConstants.USER_ONE_EMAIL}")
            .andExpect {
                status { isOk() }
                jsonPath("$.email") { value(TestConstants.USER_ONE_EMAIL) }
            }
    }

    @Test
    fun `should update user`() {
        // given
        whenever(updateUserDataUseCase.execute(any()))
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        val body = """
            {
              "firstName": "${TestConstants.APPLICATION_USER_ONE.firstName}",
              "lastName": "${TestConstants.APPLICATION_USER_ONE.lastName}",
              "password": "Password1!",
              "email": "${TestConstants.USER_ONE_EMAIL}",
              "phoneNumber": null,
              "displayName": null,
              "avatarUrl": null
            }
        """.trimIndent()

        // when & then
        mockMvc.put("/users/${TestConstants.USER_ONE_ID}") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(TestConstants.USER_ONE_ID.toString()) }
        }

        verify(updateUserDataUseCase).execute(any())
    }

    @Test
    fun `should remove current logged user`() {
        // when & then
        mockMvc.delete("/users")
            .andExpect {
                status { isNoContent() }
            }

        verify(removeCurrentLoginUserUseCase).execute()
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
        mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isConflict() }
            jsonPath("$.message") { value("User already exists") }
            jsonPath("$.status") { value(409) }
        }
    }
}
