package com.example.expense_management_server.application.account

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RemoveAccountUseCaseTest {

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    private lateinit var useCase: RemoveAccountUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = RemoveAccountUseCase(
            fetchCurrentLoginUserUseCase = fetchCurrentLoginUserUseCase,
            accountPersistencePort = accountPersistencePort
        )
    }

    @Test
    fun `should remove account when current user is account owner`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort).deleteById(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should throw AccountNotFoundException when current user is not account owner`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_TWO)

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<AccountNotFoundException> {
            useCase.execute(TestConstants.ACCOUNT_ID)
        }

        // then
        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
        verify(accountPersistencePort, never()).deleteById(TestConstants.ACCOUNT_ID)
    }
}
