package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import com.example.expense_management_server.domain.account_invitation.port.NotificationSenderPort
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CreateAccountInvitationUseCaseTest {
    @Mock
    lateinit var invitationPersistencePort: InvitationPersistencePort
    @Mock
    lateinit var accountPersistencePort: AccountPersistencePort
    @Mock
    lateinit var notificationSenderPort: NotificationSenderPort
    @Mock
    lateinit var fetchUserByEmailUseCase: FetchUserByEmailUseCase
    private lateinit var useCase: CreateAccountInvitationUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = CreateAccountInvitationUseCase(
            invitationPersistencePort,
            accountPersistencePort,
            notificationSenderPort,
            fetchUserByEmailUseCase
        )
    }

    @Test
    fun `should create invitation and send notification`() {
        // given
        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID)).thenReturn(TestConstants.ACCOUNT)
        whenever(fetchUserByEmailUseCase.execute(TestConstants.USER_TWO_EMAIL)).thenReturn(TestConstants.APPLICATION_USER_TWO)
        whenever(invitationPersistencePort.create(TestConstants.INVITATION)).thenReturn(TestConstants.INVITATION)

        // when
        val result = useCase.execute(TestConstants.INVITATION)

        // then
        assertSame(TestConstants.INVITATION, result)
        verify(fetchUserByEmailUseCase).execute(TestConstants.USER_TWO_EMAIL)
        verify(invitationPersistencePort).create(TestConstants.INVITATION)
        verify(notificationSenderPort).sendInvitationNotification(TestConstants.INVITATION)
    }

    @Test
    fun `should throw AccountNotFoundException when invitation creator is not account owner`() {
        // given
        val invitation = TestConstants.INVITATION.copy(createdBy = TestConstants.APPLICATION_USER_TWO)
        whenever(accountPersistencePort.findById(TestConstants.ACCOUNT_ID)).thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<AccountNotFoundException> { useCase.execute(invitation) }

        // then
        verify(fetchUserByEmailUseCase, never()).execute(org.mockito.kotlin.any())
        verify(invitationPersistencePort, never()).create(org.mockito.kotlin.any())
        verify(notificationSenderPort, never()).sendInvitationNotification(org.mockito.kotlin.any())
    }
}
