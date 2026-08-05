package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountValidationException
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

class DeleteMemberFromAccountUseCaseTest {

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    private lateinit var useCase: DeleteMemberFromAccountUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = DeleteMemberFromAccountUseCase(
            fetchCurrentLoginUserUseCase = fetchCurrentLoginUserUseCase,
            fetchAccountByIdUseCase = fetchAccountByIdUseCase,
            accountPersistencePort = accountPersistencePort
        )
    }

    @Test
    fun `should remove member when current user is account owner`() {
        // given
        val updatedAccount = TestConstants.ACCOUNT.copy(
            members = TestConstants.ACCOUNT.members
                .filterNot { it.applicationUserId == TestConstants.USER_TWO_ID }
                .toSet()
        )

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        whenever(
            accountPersistencePort.removeMember(
                TestConstants.ACCOUNT_ID,
                TestConstants.USER_TWO_ID
            )
        ).thenReturn(updatedAccount)

        // when
        val result = useCase.execute(
            accountId = TestConstants.ACCOUNT_ID,
            applicationUserId = TestConstants.USER_TWO_ID
        )

        // then
        assertSame(updatedAccount, result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort)
            .removeMember(TestConstants.ACCOUNT_ID, TestConstants.USER_TWO_ID)
    }

    @Test
    fun `should throw AccountValidationException when current user is not account owner`() {
        // given
        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_TWO)

        // when
        assertThrows<AccountValidationException> {
            useCase.execute(
                accountId = TestConstants.ACCOUNT_ID,
                applicationUserId = TestConstants.USER_ONE_ID
            )
        }

        // then
        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(fetchCurrentLoginUserUseCase).execute()
        verify(accountPersistencePort, never())
            .removeMember(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }
}
