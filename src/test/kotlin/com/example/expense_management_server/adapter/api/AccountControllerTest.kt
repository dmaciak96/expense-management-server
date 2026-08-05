package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.api.account.AccountController
import com.example.expense_management_server.adapter.api.account.AccountControllerAdvice
import com.example.expense_management_server.application.account.AccountCreationUseCase
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.application.account.FetchAllAccountsUseCase
import com.example.expense_management_server.application.account.RemoveAccountUseCase
import com.example.expense_management_server.application.account.UpdateAccountUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
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

class AccountControllerTest {

    @Mock
    private lateinit var accountCreationUseCase: AccountCreationUseCase

    @Mock
    private lateinit var removeAccountUseCase: RemoveAccountUseCase

    @Mock
    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    @Mock
    private lateinit var fetchAllAccountsUseCase: FetchAllAccountsUseCase

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val controller = AccountController(
            accountCreationUseCase,
            removeAccountUseCase,
            updateAccountUseCase,
            fetchAllAccountsUseCase,
            fetchAccountByIdUseCase,
            fetchCurrentLoginUserUseCase
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(AccountControllerAdvice())
            .build()
    }

    @Test
    fun `should create account`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)
        whenever(accountCreationUseCase.execute(any()))
            .thenReturn(TestConstants.ACCOUNT)

        val body = """
            {
              "name": "${TestConstants.ACCOUNT.name}",
              "currency": "${TestConstants.ACCOUNT.currency}"
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/accounts") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
            jsonPath("$.name") { value(TestConstants.ACCOUNT.name) }
            jsonPath("$.currency") { value(TestConstants.ACCOUNT.currency.name) }
        }

        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountCreationUseCase).execute(any())
    }

    @Test
    fun `should return bad request when creating account with blank name`() {
        // given
        val body = """
            {
              "name": "",
              "currency": "PLN"
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/accounts") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should return all accounts`() {
        // given
        whenever(fetchAllAccountsUseCase.execute())
            .thenReturn(listOf(TestConstants.ACCOUNT))

        // when & then
        mockMvc.get("/accounts")
            .andExpect {
                status { isOk() }
                jsonPath("$[0].id") { value(TestConstants.ACCOUNT_ID.toString()) }
                jsonPath("$[0].name") { value(TestConstants.ACCOUNT.name) }
            }

        verify(fetchAllAccountsUseCase).execute()
    }

    @Test
    fun `should return account by id`() {
        // given
        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when & then
        mockMvc.get("/accounts/${TestConstants.ACCOUNT_ID}")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
            }

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should update account`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)
        whenever(updateAccountUseCase.execute(any()))
            .thenReturn(TestConstants.ACCOUNT)

        val body = """
            {
              "name": "${TestConstants.ACCOUNT.name}",
              "currency": "${TestConstants.ACCOUNT.currency}",
              "status": "${TestConstants.ACCOUNT.status}"
            }
        """.trimIndent()

        // when & then
        mockMvc.put("/accounts/${TestConstants.ACCOUNT_ID}") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
        }

        verify(updateAccountUseCase).execute(any())
    }

    @Test
    fun `should delete account`() {
        // when & then
        mockMvc.delete("/accounts/${TestConstants.ACCOUNT_ID}")
            .andExpect {
                status { isOk() }
            }

        verify(removeAccountUseCase).execute(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should return not found error response when account does not exist`() {
        // given
        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenThrow(AccountNotFoundException("Account not found"))

        // when & then
        mockMvc.get("/accounts/${TestConstants.ACCOUNT_ID}")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.message") { value("Account not found") }
                jsonPath("$.status") { value(404) }
            }
    }
}
