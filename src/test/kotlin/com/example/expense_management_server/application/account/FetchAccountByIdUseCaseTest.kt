package com.example.expense_management_server.application.account

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class FetchAccountByIdUseCaseTest {

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    private lateinit var useCase: FetchAccountByIdUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = FetchAccountByIdUseCase(
            accountPersistencePort = accountPersistencePort,
            fetchCurrentLoginUserUseCase = fetchCurrentLoginUserUseCase
        )
    }

    @Test
    fun `should return account when current user is account owner`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        val result = useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        assertSame(TestConstants.ACCOUNT, result)

        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should return account when current user is account member`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_TWO)

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        val result = useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        assertSame(TestConstants.ACCOUNT, result)

        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should throw AccountNotFoundException when current user is neither owner nor member`() {
        // given
        val currentUser = TestConstants.APPLICATION_USER_TWO.copy(
            id = UUID.randomUUID(),
            email = "arya.stark@winterfell.com"
        )

        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(currentUser)

        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<AccountNotFoundException> {
            useCase.execute(TestConstants.ACCOUNT_ID)
        }

        // then
        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort).findById(TestConstants.ACCOUNT_ID)
    }
}
