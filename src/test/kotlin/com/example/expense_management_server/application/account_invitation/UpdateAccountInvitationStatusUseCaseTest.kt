package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.account_invitation.exception.InvitationValidationException
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

class UpdateAccountInvitationStatusUseCaseTest {
    @Mock
    lateinit var invitationPersistencePort: InvitationPersistencePort
    private lateinit var useCase: UpdateAccountInvitationStatusUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = UpdateAccountInvitationStatusUseCase(invitationPersistencePort)
    }

    @Test
    fun `should update status when invitation is pending`() {
        // given
        val updated = TestConstants.INVITATION.copy(status = AccountMemberInvitationStatus.REJECTED)
        whenever(invitationPersistencePort.findById(TestConstants.INVITATION_ID)).thenReturn(TestConstants.INVITATION)
        whenever(
            invitationPersistencePort.updateStatus(
                TestConstants.INVITATION_ID,
                AccountMemberInvitationStatus.REJECTED
            )
        ).thenReturn(updated)

        // when
        val result = useCase.execute(TestConstants.INVITATION_ID, AccountMemberInvitationStatus.REJECTED)

        // then
        assertSame(updated, result)
        verify(invitationPersistencePort).updateStatus(
            TestConstants.INVITATION_ID,
            AccountMemberInvitationStatus.REJECTED
        )
    }

    @Test
    fun `should throw InvitationValidationException when invitation is not pending`() {
        // given
        whenever(invitationPersistencePort.findById(TestConstants.INVITATION_ID))
            .thenReturn(TestConstants.INVITATION.copy(status = AccountMemberInvitationStatus.ACCEPTED))

        // when
        assertThrows<InvitationValidationException> {
            useCase.execute(TestConstants.INVITATION_ID, AccountMemberInvitationStatus.REJECTED)
        }

        // then
        verify(invitationPersistencePort, never()).updateStatus(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }
}
