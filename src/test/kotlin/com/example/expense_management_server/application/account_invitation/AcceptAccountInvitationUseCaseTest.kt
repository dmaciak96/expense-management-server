package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account_members.AddMemberToAccountUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account_invitation.exception.InvitationExpiredException
import com.example.expense_management_server.domain.account_invitation.exception.InvitationNotFoundException
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AcceptAccountInvitationUseCaseTest {
    @Mock
    lateinit var currentLoginUserUseCase: FetchCurrentLoginUserUseCase
    @Mock
    lateinit var invitationPersistencePort: InvitationPersistencePort
    @Mock
    lateinit var addMemberToAccountUseCase: AddMemberToAccountUseCase
    private lateinit var useCase: AcceptAccountInvitationUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = AcceptAccountInvitationUseCase(
            currentLoginUserUseCase,
            invitationPersistencePort,
            addMemberToAccountUseCase
        )
    }

    @Test
    fun `should accept invitation and add current user to account`() {
        // given
        whenever(invitationPersistencePort.findById(TestConstants.INVITATION_ID)).thenReturn(TestConstants.INVITATION)
        whenever(currentLoginUserUseCase.execute()).thenReturn(TestConstants.APPLICATION_USER_TWO)
        whenever(addMemberToAccountUseCase.execute(TestConstants.ACCOUNT_ID, TestConstants.USER_TWO_ID)).thenReturn(
            TestConstants.ACCOUNT
        )

        // when
        val result = useCase.execute(TestConstants.INVITATION_ID)

        // then
        assertSame(TestConstants.ACCOUNT, result)
        verify(invitationPersistencePort).updateStatus(
            TestConstants.INVITATION_ID,
            AccountMemberInvitationStatus.ACCEPTED
        )
        verify(addMemberToAccountUseCase).execute(TestConstants.ACCOUNT_ID, TestConstants.USER_TWO_ID)
    }

    @Test
    fun `should throw InvitationNotFoundException when invitation belongs to different email`() {
        // given
        whenever(invitationPersistencePort.findById(TestConstants.INVITATION_ID)).thenReturn(TestConstants.INVITATION)
        whenever(currentLoginUserUseCase.execute()).thenReturn(TestConstants.APPLICATION_USER_ONE)

        // when
        assertThrows<InvitationNotFoundException> { useCase.execute(TestConstants.INVITATION_ID) }

        // then
        verify(invitationPersistencePort, never()).updateStatus(org.mockito.kotlin.any(), org.mockito.kotlin.any())
        verify(addMemberToAccountUseCase, never()).execute(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }

    @Test
    fun `should throw InvitationExpiredException when invitation is expired`() {
        // given
        whenever(invitationPersistencePort.findById(TestConstants.INVITATION_ID))
            .thenReturn(TestConstants.INVITATION.copy(status = AccountMemberInvitationStatus.EXPIRED))
        whenever(currentLoginUserUseCase.execute()).thenReturn(TestConstants.APPLICATION_USER_TWO)

        // when
        assertThrows<InvitationExpiredException> { useCase.execute(TestConstants.INVITATION_ID) }

        // then
        verify(invitationPersistencePort, never()).updateStatus(org.mockito.kotlin.any(), org.mockito.kotlin.any())
        verify(addMemberToAccountUseCase, never()).execute(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }
}
