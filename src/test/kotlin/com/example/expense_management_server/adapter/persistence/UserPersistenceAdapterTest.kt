package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.persistence.model.ApplicationUserEntity
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.*

class UserPersistenceAdapterTest {

    private val userRepository: UserRepository = mock()
    private val userPersistenceAdapter = UserPersistenceAdapter(userRepository)

    @Test
    fun `when proper domain object was provided then should create new user`() {
        // given
        val expected = ApplicationUserEntity.fromDomain(TestConstants.APPLICATION_USER_ONE)
        `when`(userRepository.save(expected)).thenReturn(
            expected
        )

        // when
        val result = userPersistenceAdapter.create(TestConstants.APPLICATION_USER_ONE)

        // then
        verify(userRepository).save(expected)
        assertThat(result, equalTo(TestConstants.APPLICATION_USER_ONE))
    }

    @Test
    fun `when user with provided e-mail does not exists then should throw UserNotFoundException`() {
        // given
        `when`(userRepository.findByEmail(TestConstants.USER_ONE_EMAIL)).thenReturn(null)

        // when & then
        assertThrows<UserNotFoundException> {
            userPersistenceAdapter.findByEmail(TestConstants.USER_ONE_EMAIL)
        }
    }


    @Test
    fun `when user with provided id does not exists then should throw UserNotFoundException`() {
        // given
        `when`(userRepository.findById(TestConstants.USER_ONE_ID)).thenReturn(Optional.empty())

        // when & then
        assertThrows<UserNotFoundException> {
            userPersistenceAdapter.findById(TestConstants.USER_ONE_ID)
        }
    }

    @Test
    fun `when user with provided e-mail exists then should return user`() {
        // given
        `when`(userRepository.findByEmail(TestConstants.USER_ONE_EMAIL)).thenReturn(
            ApplicationUserEntity.fromDomain(
                TestConstants.APPLICATION_USER_ONE
            )
        )

        // when
        val result = userPersistenceAdapter.findByEmail(TestConstants.USER_ONE_EMAIL)

        // then
        verify(userRepository).findByEmail(TestConstants.USER_ONE_EMAIL)
        assertThat(result, equalTo(TestConstants.APPLICATION_USER_ONE))
    }


    @Test
    fun `when user with provided id exists then should return user`() {
        // given
        `when`(userRepository.findById(TestConstants.USER_ONE_ID)).thenReturn(
            Optional.of(
                ApplicationUserEntity.fromDomain(
                    TestConstants.APPLICATION_USER_ONE
                )
            )
        )

        // when
        val result = userPersistenceAdapter.findById(TestConstants.USER_ONE_ID)

        // then
        assertThat(result, equalTo(TestConstants.APPLICATION_USER_ONE))
    }

    @Test
    fun `when user exists and provided proper data then should update user`() {
        // given
        val expected = TestConstants.APPLICATION_USER_TWO.copy(id = TestConstants.USER_ONE_ID)
        `when`(userRepository.findById(TestConstants.USER_ONE_ID)).thenReturn(
            Optional.of(
                ApplicationUserEntity.fromDomain(
                    TestConstants.APPLICATION_USER_ONE
                )
            )
        )
        `when`(userRepository.save(ApplicationUserEntity.fromDomain(expected))).thenReturn(
            ApplicationUserEntity.fromDomain(
                expected
            )
        )

        // when
        val result =
            userPersistenceAdapter.update(TestConstants.APPLICATION_USER_TWO.copy(id = TestConstants.USER_ONE_ID))

        // then
        verify(userRepository).findById(expected.id)
        verify(userRepository).save(ApplicationUserEntity.fromDomain(expected))
        assertThat(result, equalTo(expected))
    }

    @Test
    fun `when user not exists during update then should throws UserNotFoundException`() {
        // given
        `when`(userRepository.findById(TestConstants.USER_ONE_ID)).thenReturn(Optional.empty())

        // when & then
        assertThrows<UserNotFoundException> {
            userPersistenceAdapter.update(TestConstants.APPLICATION_USER_ONE)
        }
    }

    @Test
    fun `when user exists with provided id then should delete user data`() {
        // given
        `when`(userRepository.findById(TestConstants.USER_ONE_ID)).thenReturn(
            Optional.of(
                ApplicationUserEntity.fromDomain(
                    TestConstants.APPLICATION_USER_ONE
                )
            )
        )

        // when
        userPersistenceAdapter.deleteById(TestConstants.USER_ONE_ID)

        // then
        verify(userRepository).findById(TestConstants.USER_ONE_ID)
        verify(userRepository).deleteById(TestConstants.USER_ONE_ID)
    }

    @Test
    fun `when user not exists with provided id then should throws UserNotFoundException`() {
        // given
        `when`(userRepository.findById(TestConstants.USER_ONE_ID)).thenReturn(Optional.empty())

        // when & then
        assertThrows<UserNotFoundException> {
            userPersistenceAdapter.deleteById(TestConstants.USER_ONE_ID)
        }
    }
}