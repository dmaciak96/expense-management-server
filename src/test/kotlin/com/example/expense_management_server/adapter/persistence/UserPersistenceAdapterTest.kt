package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.persistence.model.ApplicationUserEntity
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
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

class UserPersistenceAdapterTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var adapter: UserPersistenceAdapter

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        adapter = UserPersistenceAdapter(
            userRepository = userRepository
        )
    }

    @Test
    fun `should create user`() {
        // given
        val entity = ApplicationUserEntity.fromDomain(TestConstants.APPLICATION_USER_ONE)

        whenever(userRepository.save(entity))
            .thenReturn(entity)

        // when
        val result = adapter.create(TestConstants.APPLICATION_USER_ONE)

        // then
        assertEquals(TestConstants.APPLICATION_USER_ONE, result)

        verify(userRepository).save(entity)
    }

    @Test
    fun `should find user by email`() {
        // given
        val entity = ApplicationUserEntity.fromDomain(TestConstants.APPLICATION_USER_ONE)

        whenever(userRepository.findByEmail(TestConstants.USER_ONE_EMAIL))
            .thenReturn(entity)

        // when
        val result = adapter.findByEmail(TestConstants.USER_ONE_EMAIL)

        // then
        assertEquals(TestConstants.APPLICATION_USER_ONE, result)

        verify(userRepository).findByEmail(TestConstants.USER_ONE_EMAIL)
    }

    @Test
    fun `should throw UserNotFoundException when email does not exist`() {
        // given
        whenever(userRepository.findByEmail(TestConstants.USER_ONE_EMAIL))
            .thenReturn(null)

        // when & then
        assertThrows<UserNotFoundException> {
            adapter.findByEmail(TestConstants.USER_ONE_EMAIL)
        }

        verify(userRepository).findByEmail(TestConstants.USER_ONE_EMAIL)
    }

    @Test
    fun `should find user by id`() {
        // given
        val entity = ApplicationUserEntity.fromDomain(TestConstants.APPLICATION_USER_ONE)

        whenever(userRepository.findById(TestConstants.USER_ONE_ID.toString()))
            .thenReturn(Optional.of(entity))

        // when
        val result = adapter.findById(TestConstants.USER_ONE_ID)

        // then
        assertEquals(TestConstants.APPLICATION_USER_ONE, result)

        verify(userRepository).findById(TestConstants.USER_ONE_ID.toString())
    }

    @Test
    fun `should not delete user when user does not exist`() {
        // given
        val id = UUID.randomUUID()

        whenever(userRepository.findById(id.toString()))
            .thenReturn(Optional.empty())

        // when & then
        assertThrows<UserNotFoundException> {
            adapter.deleteById(id)
        }

        verify(userRepository).findById(id.toString())
        verify(userRepository, never()).deleteById(id.toString())
    }
}
