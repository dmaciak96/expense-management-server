package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchUserByEmailUseCaseTest {

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    @Mock
    private lateinit var applicationUser: ApplicationUser

    @InjectMocks
    private lateinit var useCase: FetchUserByEmailUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should return active user`() {
        // given
        val email = "user@example.com"

        whenever(userPersistencePort.findByEmail(email))
            .thenReturn(applicationUser)

        whenever(applicationUser.status)
            .thenReturn(ApplicationUserStatus.ACTIVE)

        // when
        val result = useCase.execute(email)

        // then
        assertSame(applicationUser, result)

        verify(userPersistencePort).findByEmail(email)
    }

    @Test
    fun `should throw UserNotFoundException when user is not active`() {
        // given
        val email = "user@example.com"
        val inactiveStatus = ApplicationUserStatus.entries
            .first { it != ApplicationUserStatus.ACTIVE }

        whenever(userPersistencePort.findByEmail(email))
            .thenReturn(applicationUser)

        whenever(applicationUser.status)
            .thenReturn(inactiveStatus)

        // when
        val exception = assertThrows<UserNotFoundException> {
            useCase.execute(email)
        }

        // then
        assert(exception.message == "User $email not found")

        verify(userPersistencePort).findByEmail(email)
    }

    @Test
    fun `should propagate UserNotFoundException from persistence port`() {
        // given
        val email = "missing@example.com"

        whenever(userPersistencePort.findByEmail(email))
            .thenThrow(UserNotFoundException("User not found"))

        // when
        assertThrows<UserNotFoundException> {
            useCase.execute(email)
        }

        // then
        verify(userPersistencePort).findByEmail(email)
    }
}
