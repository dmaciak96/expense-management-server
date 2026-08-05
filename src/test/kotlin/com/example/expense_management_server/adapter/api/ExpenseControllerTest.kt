package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.api.expense.ExpenseController
import com.example.expense_management_server.adapter.api.expense.ExpenseControllerAdvice
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.application.expense.AddExpenseToAccountUseCase
import com.example.expense_management_server.application.expense.DeleteExpenseFromAccountUseCase
import com.example.expense_management_server.application.expense.FetchAllExpensesFromAccount
import com.example.expense_management_server.domain.account.exception.ExpenseNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ExpenseControllerTest {

    @Mock
    private lateinit var fetchUserByEmailUseCase: FetchUserByEmailUseCase

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    @Mock
    private lateinit var addExpenseToAccountUseCase: AddExpenseToAccountUseCase

    @Mock
    private lateinit var deleteExpenseFromAccountUseCase: DeleteExpenseFromAccountUseCase

    @Mock
    private lateinit var fetchAllExpensesFromAccount: FetchAllExpensesFromAccount

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val controller = ExpenseController(
            fetchUserByEmailUseCase,
            fetchCurrentLoginUserUseCase,
            addExpenseToAccountUseCase,
            deleteExpenseFromAccountUseCase,
            fetchAllExpensesFromAccount
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(ExpenseControllerAdvice())
            .build()
    }

    @Test
    fun `should add expense to account`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)
        whenever(fetchUserByEmailUseCase.execute(TestConstants.USER_TWO_EMAIL))
            .thenReturn(TestConstants.APPLICATION_USER_TWO)
        whenever(
            addExpenseToAccountUseCase.execute(
                any(),
                eq(TestConstants.ACCOUNT_ID)
            )
        ).thenReturn(TestConstants.ACCOUNT)

        val body = """
            {
              "paidByEmail": "${TestConstants.USER_TWO_EMAIL}",
              "name": "${TestConstants.EXPENSE.name}",
              "monetaryAmount": ${TestConstants.EXPENSE.monetaryAmount}
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/accounts/${TestConstants.ACCOUNT_ID}/expenses") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
        }

        verify(addExpenseToAccountUseCase).execute(
            any(),
            eq(TestConstants.ACCOUNT_ID)
        )
    }

    @Test
    fun `should return bad request when monetary amount is not positive`() {
        // given
        val body = """
            {
              "paidByEmail": "${TestConstants.USER_TWO_EMAIL}",
              "name": "Dinner",
              "monetaryAmount": 0
            }
        """.trimIndent()

        // when & then
        mockMvc.post("/accounts/${TestConstants.ACCOUNT_ID}/expenses") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should delete expense from account`() {
        // given
        whenever(
            deleteExpenseFromAccountUseCase.execute(
                TestConstants.EXPENSE_ID,
                TestConstants.ACCOUNT_ID
            )
        ).thenReturn(TestConstants.ACCOUNT)

        // when & then
        mockMvc.delete(
            "/accounts/${TestConstants.ACCOUNT_ID}/expenses/${TestConstants.EXPENSE_ID}"
        ).andExpect {
            status { isOk() }
            jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
        }

        verify(deleteExpenseFromAccountUseCase)
            .execute(TestConstants.EXPENSE_ID, TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should return all expenses from account`() {
        // given
        whenever(fetchAllExpensesFromAccount.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT.expenses.toList())

        // when & then
        mockMvc.get("/accounts/${TestConstants.ACCOUNT_ID}/expenses")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
            }

        verify(fetchAllExpensesFromAccount).execute(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should return not found error response when expense does not exist`() {
        // given
        whenever(
            deleteExpenseFromAccountUseCase.execute(
                TestConstants.EXPENSE_ID,
                TestConstants.ACCOUNT_ID
            )
        ).thenThrow(ExpenseNotFoundException("Expense not found"))

        // when & then
        mockMvc.delete(
            "/accounts/${TestConstants.ACCOUNT_ID}/expenses/${TestConstants.EXPENSE_ID}"
        ).andExpect {
            status { isNotFound() }
            jsonPath("$.message") { value("Expense not found") }
            jsonPath("$.status") { value(404) }
        }
    }
}
