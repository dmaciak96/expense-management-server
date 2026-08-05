package com.example.expense_management_server.application.expense

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class DeleteExpenseFromAccountUseCaseTest {

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    private lateinit var useCase: DeleteExpenseFromAccountUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = DeleteExpenseFromAccountUseCase(
            fetchAccountByIdUseCase = fetchAccountByIdUseCase,
            userPersistencePort = accountPersistencePort
        )
    }

    @Test
    fun `should remove expense when expense exists in account`() {
        // given
        val updatedAccount = TestConstants.ACCOUNT.copy(
            expenses = emptySet()
        )

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        whenever(
            accountPersistencePort.removeExpense(
                TestConstants.ACCOUNT_ID,
                TestConstants.EXPENSE_ID
            )
        ).thenReturn(updatedAccount)

        // when
        val result = useCase.execute(
            expenseId = TestConstants.EXPENSE_ID,
            accountId = TestConstants.ACCOUNT_ID
        )

        // then
        assertSame(updatedAccount, result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort)
            .removeExpense(TestConstants.ACCOUNT_ID, TestConstants.EXPENSE_ID)
    }

    @Test
    fun `should throw ExpenseNotFoundException when expense does not exist in account`() {
        // given
        val expenseId = UUID.randomUUID()

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<ExpenseNotFoundException> {
            useCase.execute(
                expenseId = expenseId,
                accountId = TestConstants.ACCOUNT_ID
            )
        }

        // then
        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort, never())
            .removeExpense(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }
}
