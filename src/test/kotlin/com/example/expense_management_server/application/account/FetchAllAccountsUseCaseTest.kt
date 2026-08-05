package com.example.expense_management_server.application.account

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class FetchAllAccountsUseCaseTest {

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    private lateinit var useCase: FetchAllAccountsUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = FetchAllAccountsUseCase(
            fetchCurrentLoginUserUseCase = fetchCurrentLoginUserUseCase,
            accountPersistencePort = accountPersistencePort
        )
    }

    @Test
    fun `should return accounts created by current user and accounts where current user is member`() {
        // given
        val createdAccount = TestConstants.ACCOUNT
        val memberedAccount = TestConstants.ACCOUNT.copy(
            id = UUID.randomUUID(),
            name = "Winterfell Budget"
        )

        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        whenever(accountPersistencePort.findAllByCreatorId(TestConstants.USER_ONE_ID))
            .thenReturn(listOf(createdAccount))

        whenever(accountPersistencePort.findAllByMemberId(TestConstants.USER_ONE_ID))
            .thenReturn(listOf(memberedAccount))

        // when
        val result = useCase.execute()

        // then
        assertEquals(listOf(createdAccount, memberedAccount), result)

        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort).findAllByCreatorId(TestConstants.USER_ONE_ID)
        verify(accountPersistencePort).findAllByMemberId(TestConstants.USER_ONE_ID)
    }

    @Test
    fun `should return empty list when current user has no accounts`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        whenever(accountPersistencePort.findAllByCreatorId(TestConstants.USER_ONE_ID))
            .thenReturn(emptyList())

        whenever(accountPersistencePort.findAllByMemberId(TestConstants.USER_ONE_ID))
            .thenReturn(emptyList())

        // when
        val result = useCase.execute()

        // then
        assertEquals(emptyList<Any>(), result)

        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort).findAllByCreatorId(TestConstants.USER_ONE_ID)
        verify(accountPersistencePort).findAllByMemberId(TestConstants.USER_ONE_ID)
    }
}
