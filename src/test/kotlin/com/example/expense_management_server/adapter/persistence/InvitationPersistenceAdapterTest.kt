package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.persistence.model.AccountMemberInvitationEntity
import com.example.expense_management_server.adapter.persistence.repository.InvitationRepository
import com.example.expense_management_server.domain.account_invitation.exception.InvitationNotFoundException
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional
import java.util.UUID

class InvitationPersistenceAdapterTest {

    @Mock
    private lateinit var invitationRepository: InvitationRepository

    private lateinit var adapter: InvitationPersistenceAdapter

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        adapter = InvitationPersistenceAdapter(
            invitationRepository = invitationRepository
        )
    }

    @Test
    fun `should create invitation`() {
        // given
        val entity = AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)

        whenever(invitationRepository.save(entity))
            .thenReturn(entity)

        // when
        val result = adapter.create(TestConstants.INVITATION)

        // then
        assertEquals(TestConstants.INVITATION, result)

        verify(invitationRepository).save(entity)
    }

    @Test
    fun `should find invitation by id`() {
        // given
        val entity = AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)

        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString()))
            .thenReturn(Optional.of(entity))

        // when
        val result = adapter.findById(TestConstants.INVITATION_ID)

        // then
        assertEquals(TestConstants.INVITATION, result)

        verify(invitationRepository).findById(TestConstants.INVITATION_ID.toString())
    }

    @Test
    fun `should update invitation status`() {
        // given
        val entity = AccountMemberInvitationEntity.fromDomain(TestConstants.INVITATION)
        val updatedEntity = entity.copy(status = AccountMemberInvitationStatus.ACCEPTED)

        whenever(invitationRepository.findById(TestConstants.INVITATION_ID.toString()))
            .thenReturn(Optional.of(entity))

        whenever(invitationRepository.save(updatedEntity))
            .thenReturn(updatedEntity)

        // when
        val result = adapter.updateStatus(
            TestConstants.INVITATION_ID,
            AccountMemberInvitationStatus.ACCEPTED
        )

        // then
        assertEquals(AccountMemberInvitationStatus.ACCEPTED, result.status)

        verify(invitationRepository).findById(TestConstants.INVITATION_ID.toString())
        verify(invitationRepository).save(updatedEntity)
    }

    @Test
    fun `should not delete invitation when invitation does not exist`() {
        // given
        val id = UUID.randomUUID()

        whenever(invitationRepository.findById(id.toString()))
            .thenReturn(Optional.empty())

        // when & then
        assertThrows<InvitationNotFoundException> {
            adapter.deleteById(id)
        }

        verify(invitationRepository).findById(id.toString())
        verify(invitationRepository, never()).deleteById(id.toString())
    }
}
