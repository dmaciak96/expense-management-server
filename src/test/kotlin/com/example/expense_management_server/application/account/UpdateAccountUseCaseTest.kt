package com.example.expense_management_server.application.account

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.exception.AccountValidationException
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
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

class UpdateAccountUseCaseTest {

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    private lateinit var useCase: UpdateAccountUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = UpdateAccountUseCase(
            accountPersistencePort = accountPersistencePort
        )
    }

    @Test
    fun `should update account when current user is account owner`() {
        // given
        val accountToUpdate = TestConstants.ACCOUNT.copy(
            name = "Updated North Budget",
            currency = Currency.EUR,
            status = AccountStatus.INACTIVE
        )

        val expectedAccount = TestConstants.ACCOUNT.copy(
            name = accountToUpdate.name,
            currency = accountToUpdate.currency,
            status = accountToUpdate.status
        )

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        whenever(accountPersistencePort.update(expectedAccount))
            .thenReturn(expectedAccount)

        // when
        val result = useCase.execute(accountToUpdate)

        // then
        assertSame(expectedAccount, result)

        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort).update(expectedAccount)
    }

    @Test
    fun `should throw AccountValidationException when account member tries to update account`() {
        // given
        val accountToUpdate = TestConstants.ACCOUNT.copy(
            createdBy = TestConstants.APPLICATION_USER_TWO,
            name = "Updated North Budget"
        )

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<AccountValidationException> {
            useCase.execute(accountToUpdate)
        }

        // then
        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort, never()).update(org.mockito.kotlin.any())
    }

    @Test
    fun `should throw AccountNotFoundException when user is neither account owner nor member`() {
        // given
        val currentUser = TestConstants.APPLICATION_USER_TWO.copy(
            id = UUID.randomUUID(),
            email = "arya.stark@winterfell.com"
        )

        val accountToUpdate = TestConstants.ACCOUNT.copy(
            createdBy = currentUser,
            name = "Updated North Budget"
        )

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<AccountNotFoundException> {
            useCase.execute(accountToUpdate)
        }

        // then
        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort, never()).update(org.mockito.kotlin.any())
    }
}
