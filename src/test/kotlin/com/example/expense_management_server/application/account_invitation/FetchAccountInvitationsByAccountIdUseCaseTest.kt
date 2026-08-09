package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchAccountInvitationsByAccountIdUseCaseTest {
    @Mock
    lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase
    @Mock
    lateinit var invitationPersistencePort: InvitationPersistencePort
    @Mock
    lateinit var accountPersistencePort: AccountPersistencePort
    private lateinit var useCase: FetchAccountInvitationsByAccountIdUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = FetchAccountInvitationsByAccountIdUseCase(
            fetchCurrentLoginUserUseCase,
            invitationPersistencePort,
            accountPersistencePort
        )
    }

    @Test
    fun `should return invitations when current user is account owner`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute()).thenReturn(TestConstants.APPLICATION_USER_ONE)
        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID)).thenReturn(TestConstants.ACCOUNT)
        whenever(invitationPersistencePort.findAllByAccountId(TestConstants.ACCOUNT_ID)).thenReturn(listOf(TestConstants.INVITATION))

        // when
        val result = useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        assertEquals(listOf(TestConstants.INVITATION), result)
        verify(invitationPersistencePort).findAllByAccountId(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should throw AccountNotFoundException when current user is not account owner`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute()).thenReturn(TestConstants.APPLICATION_USER_TWO)
        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID)).thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<AccountNotFoundException> { useCase.execute(TestConstants.ACCOUNT_ID) }

        // then
        verify(invitationPersistencePort, never()).findAllByAccountId(org.mockito.kotlin.any())
    }
}
