package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.exception.UserNotLoggedInException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class FetchCurrentLoginUserUseCaseTest {

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    @Mock
    private lateinit var authentication: Authentication

    @Mock
    private lateinit var securityContext: SecurityContext

    @Mock
    private lateinit var applicationUser: ApplicationUser

    @InjectMocks
    private lateinit var useCase: FetchCurrentLoginUserUseCase

    private lateinit var mocks: AutoCloseable

    @BeforeEach
    fun setUp() {
        mocks = MockitoAnnotations.openMocks(this)
        SecurityContextHolder.setContext(securityContext)
    }

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
        mocks.close()
    }

    @Test
    fun `should return currently logged-in user`() {
        // given
        val email = "user@example.com"

        whenever(securityContext.authentication).thenReturn(authentication)
        whenever(authentication.name).thenReturn(email)
        whenever(userPersistencePort.findByEmail(email)).thenReturn(applicationUser)

        // when
        val result = useCase.execute()

        // then
        assertSame(applicationUser, result)

        verify(userPersistencePort).findByEmail(email)
    }

    @Test
    fun `should throw UserNotLoggedInException when authentication is null`() {
        // given
        whenever(securityContext.authentication).thenReturn(null)

        // when
        assertThrows<UserNotLoggedInException> {
            useCase.execute()
        }

        // then
        verify(userPersistencePort, never()).findByEmail(org.mockito.kotlin.any())
    }
}
