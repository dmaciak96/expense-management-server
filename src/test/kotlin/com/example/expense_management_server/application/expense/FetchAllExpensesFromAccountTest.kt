package com.example.expense_management_server.application.expense

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchAllExpensesFromAccountTest {

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    private lateinit var useCase: FetchAllExpensesFromAccount

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = FetchAllExpensesFromAccount(
            fetchAccountByIdUseCase = fetchAccountByIdUseCase
        )
    }

    @Test
    fun `should return all expenses from account`() {
        // given
        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        val result = useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        assertEquals(TestConstants.ACCOUNT.expenses.toList(), result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should return empty list when account has no expenses`() {
        // given
        val account = TestConstants.ACCOUNT.copy(expenses = emptySet())

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(account)

        // when
        val result = useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        assertEquals(emptyList<Any>(), result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
    }
}
