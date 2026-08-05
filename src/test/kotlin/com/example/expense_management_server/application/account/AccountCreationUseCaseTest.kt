package com.example.expense_management_server.application.account

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AccountCreationUseCaseTest {

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    private lateinit var useCase: AccountCreationUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = AccountCreationUseCase(
            accountPersistencePort = accountPersistencePort,
        )
    }

    @Test
    fun `should create account`() {
        // given
        whenever(accountPersistencePort.create(TestConstants.ACCOUNT))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        val result = useCase.execute(TestConstants.ACCOUNT)

        // then
        assertSame(TestConstants.ACCOUNT, result)
        verify(accountPersistencePort).create(TestConstants.ACCOUNT)
    }

    @Test
    fun `should propagate exception when account creation fails`() {
        // given
        val exception = RuntimeException("Account creation failed")

        whenever(accountPersistencePort.create(TestConstants.ACCOUNT))
            .thenThrow(exception)

        // when
        val result = assertThrows<RuntimeException> {
            useCase.execute(TestConstants.ACCOUNT)
        }

        // then
        assertSame(exception, result)
        verify(accountPersistencePort).create(TestConstants.ACCOUNT)
    }
}
