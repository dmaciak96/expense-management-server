package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.persistence.model.AccountMemberInvitationEntity
import com.example.expense_management_server.adapter.persistence.repository.InvitationRepository
import com.example.expense_management_server.domain.account_invitation.exception.InvitationNotFoundException
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class InvitationPersistenceAdapterTest {

    private val invitationRepository: InvitationRepository = mock()
    private val invitationPersistenceAdapter = InvitationPersistenceAdapter(invitationRepository)

    @Test
    fun `when provide proper domain model then should create account invitation`() {
        // given
        val expected = AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)
        whenever(invitationRepository.save(AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION))).thenReturn(
            expected
        )

        // when
        val result = invitationPersistenceAdapter.create(TestConstants.INVITATION)

        // then
        verify(invitationRepository).save(expected)
        assertThat(result, equalTo(TestConstants.INVITATION))
    }

    @Test
    fun `when invitation with provided id exists then should return proper invitation object`() {
        // given
        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString())).thenReturn(
            Optional.of(
                AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)
            )
        )

        // when
        val result = invitationPersistenceAdapter.findById(TestConstants.INVITATION_ID)

        // then
        verify(invitationRepository).findById(TestConstants.INVITATION_ID.toString())
        assertThat(result, equalTo(TestConstants.INVITATION))
    }

    @Test
    fun `when invitation with provided id not exists then should throws InvitationNotFoundException`() {
        // given
        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString())).thenReturn(Optional.empty())

        // when & then
        assertThrows<InvitationNotFoundException> {
            invitationPersistenceAdapter.findById(TestConstants.INVITATION_ID)
        }
    }

    @Test
    fun `when invitation exists then should delete invitation`() {
        // given
        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString())).thenReturn(
            Optional.of(
                AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)
            )
        )
        doNothing().`when`(invitationRepository).deleteById(TestConstants.INVITATION_ID.toString())

        // when
        invitationPersistenceAdapter.deleteById(TestConstants.INVITATION_ID)

        // then
        verify(invitationRepository).deleteById(TestConstants.INVITATION_ID.toString())
    }

    @Test
    fun `when invitation not exists during deletion then should throws InvitationNotFoundException`() {
        // given
        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString())).thenReturn(Optional.empty())

        // when & then
        assertThrows<InvitationNotFoundException> {
            invitationPersistenceAdapter.deleteById(TestConstants.INVITATION_ID)
        }
    }

    @Test
    fun `when invitation not exists during update status then should throws InvitationNotFoundException`() {
        // given
        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString())).thenReturn(Optional.empty())

        // when & then
        assertThrows<InvitationNotFoundException> {
            invitationPersistenceAdapter.updateStatus(
                TestConstants.INVITATION_ID,
                AccountMemberInvitationStatus.REJECTED
            )
        }
    }

    @Test
    fun `when correct status and invitation id then should update invitation status`() {
        // given
        val expected = TestConstants.INVITATION.copy(status = AccountMemberInvitationStatus.REJECTED)
        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString())).thenReturn(
            Optional.of(
                AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)
            )
        )
        whenever(invitationRepository.save(AccountMemberInvitationEntity.fromDomain(expected))).thenReturn(
            AccountMemberInvitationEntity.fromDomain(expected)
        )

        // when
        val result = invitationPersistenceAdapter.updateStatus(
            TestConstants.INVITATION_ID,
            AccountMemberInvitationStatus.REJECTED
        )

        // then
        verify(invitationRepository).findById(TestConstants.INVITATION_ID.toString())
        verify(invitationRepository).save(AccountMemberInvitationEntity.fromDomain(expected))
        assertThat(result, equalTo(expected))
    }

    @Test
    fun `when invitation exists by accountID then should return proper list`() {
        // given
        whenever(invitationRepository.findAllByAccountId(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(listOf(AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)))

        // when
        val result = invitationPersistenceAdapter.findAllByAccountId(TestConstants.ACCOUNT_ID)

        // then
        verify(invitationRepository).findAllByAccountId(TestConstants.ACCOUNT_ID.toString())
        assertThat(result, hasSize(1))
        assertThat(result, containsInAnyOrder(TestConstants.INVITATION))
    }

    @Test
    fun `when user not created account then should return empty list`() {
        // given
        whenever(invitationRepository.findAllByAccountId(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(emptyList())

        // when
        val result = invitationPersistenceAdapter.findAllByAccountId(TestConstants.ACCOUNT_ID)

        // then
        verify(invitationRepository).findAllByAccountId(TestConstants.ACCOUNT_ID.toString())
        assertThat(result, hasSize(0))
    }
}