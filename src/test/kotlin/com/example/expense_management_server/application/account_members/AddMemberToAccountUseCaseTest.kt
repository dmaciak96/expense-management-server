package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.FetchUserByIdUseCase
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

class AddMemberToAccountUseCaseTest {

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    @Mock
    private lateinit var fetchUserByIdUseCase: FetchUserByIdUseCase

    @Mock
    private lateinit var accountPersistencePort: AccountPersistencePort

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    private lateinit var useCase: AddMemberToAccountUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = AddMemberToAccountUseCase(
            fetchAccountByIdUseCase = fetchAccountByIdUseCase,
            fetchUserByIdUseCase = fetchUserByIdUseCase,
            accountPersistencePort = accountPersistencePort,
            fetchCurrentLoginUserUseCase = fetchCurrentLoginUserUseCase
        )
    }

    @Test
    fun `should add member when current user is account owner`() {
        // given
        val account = TestConstants.ACCOUNT.copy(
            members = setOf(TestConstants.APPLICATION_USER_ONE.toAccountMember())
        )
        val member = TestConstants.APPLICATION_USER_TWO.toAccountMember()
        val updatedAccount = account.copy(
            members = account.members + member
        )

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(account)

        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        whenever(fetchUserByIdUseCase.execute(TestConstants.USER_TWO_ID))
            .thenReturn(TestConstants.APPLICATION_USER_TWO)

        whenever(
            accountPersistencePort.addAccountMember(
                accountId = TestConstants.ACCOUNT_ID,
                accountMember = member
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
        verify(fetchUserByIdUseCase).execute(TestConstants.USER_TWO_ID)
        verify(accountPersistencePort).addAccountMember(TestConstants.ACCOUNT_ID, member)
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
                applicationUserId = TestConstants.USER_TWO_ID
            )
        }

        // then
        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(fetchCurrentLoginUserUseCase).execute()
        verify(fetchUserByIdUseCase, never()).execute(org.mockito.kotlin.any())
        verify(accountPersistencePort, never())
            .addAccountMember(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }

    @Test
    fun `should throw AccountValidationException when member already exists in account`() {
        // given
        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        // when
        assertThrows<AccountValidationException> {
            useCase.execute(
                accountId = TestConstants.ACCOUNT_ID,
                applicationUserId = TestConstants.USER_TWO_ID
            )
        }

        // then
        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
        verify(fetchCurrentLoginUserUseCase).execute()
        verify(fetchUserByIdUseCase, never()).execute(org.mockito.kotlin.any())
        verify(accountPersistencePort, never())
            .addAccountMember(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }
}
