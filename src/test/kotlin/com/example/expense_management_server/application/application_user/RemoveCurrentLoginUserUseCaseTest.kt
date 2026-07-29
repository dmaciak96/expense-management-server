package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.exception.UserNotLoggedInException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class RemoveCurrentLoginUserUseCaseTest {

    @Mock
    private lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    @Mock
    private lateinit var applicationUser: ApplicationUser

    @InjectMocks
    private lateinit var useCase: RemoveCurrentLoginUserUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should delete currently logged-in user`() {
        // given
        val userId = UUID.randomUUID()

        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenReturn(applicationUser)

        whenever(applicationUser.id)
            .thenReturn(userId)

        // when
        useCase.execute()

        // then
        inOrder(fetchCurrentLoginUserUseCase, userPersistencePort) {
            verify(fetchCurrentLoginUserUseCase).execute()
            verify(userPersistencePort).deleteById(userId)
        }
    }

    @Test
    fun `should not delete user when fetching current user fails`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute())
            .thenThrow(UserNotLoggedInException())

        // when
        assertThrows<UserNotLoggedInException> {
            useCase.execute()
        }

        // then
        verify(userPersistencePort, never())
            .deleteById(org.mockito.kotlin.any())
    }
}
