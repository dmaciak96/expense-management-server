package com.example.expense_management_server.application.expense

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.exception.ExpenseValidationException
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

class AddExpenseToAccountUseCaseTest {

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    private lateinit var useCase: AddExpenseToAccountUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = AddExpenseToAccountUseCase(
            fetchAccountByIdUseCase = fetchAccountByIdUseCase,
            accountPersistencePort = accountPersistencePort
        )
    }

    @Test
    fun `should add expense when expense is paid by account owner`() {
        // given
        val updatedAccount = TestConstants.ACCOUNT.copy(
            expenses = TestConstants.ACCOUNT.expenses + TestConstants.EXPENSE
        )

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        whenever(
            accountPersistencePort.addExpense(
                TestConstants.EXPENSE,
                TestConstants.ACCOUNT_ID
            )
        ).thenReturn(updatedAccount)

        // when
        val result = useCase.execute(
            expenseToAdd = TestConstants.EXPENSE,
            accountId = TestConstants.ACCOUNT_ID
        )

        // then
        assertSame(updatedAccount, result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort)
            .addExpense(TestConstants.EXPENSE, TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should add expense when expense is paid by account member`() {
        // given
        val expense = TestConstants.EXPENSE.copy(
            paidBy = TestConstants.APPLICATION_USER_TWO
        )
        val updatedAccount = TestConstants.ACCOUNT.copy(
            expenses = TestConstants.ACCOUNT.expenses + expense
        )

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        whenever(
            accountPersistencePort.addExpense(
                expense,
                TestConstants.ACCOUNT_ID
            )
        ).thenReturn(updatedAccount)

        // when
        val result = useCase.execute(
            expenseToAdd = expense,
            accountId = TestConstants.ACCOUNT_ID
        )

        // then
        assertSame(updatedAccount, result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort)
            .addExpense(expense, TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should throw ExpenseValidationException when expense is paid by user outside account`() {
        // given
        val externalUser = TestConstants.APPLICATION_USER_TWO.copy(
            id = UUID.randomUUID(),
            email = "tyrion.lannister@casterly-rock.com"
        )
        val expense = TestConstants.EXPENSE.copy(
            paidBy = externalUser
        )

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<ExpenseValidationException> {
            useCase.execute(
                expenseToAdd = expense,
                accountId = TestConstants.ACCOUNT_ID
            )
        }

        // then
        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort, never())
            .addExpense(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }
}
