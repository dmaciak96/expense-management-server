package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MongoUserDetailsServiceTest {

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    private lateinit var service: MongoUserDetailsService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        service = MongoUserDetailsService(
            userPersistencePort = userPersistencePort
        )
    }

    @Test
    fun `should load user details by email`() {
        // given
        whenever(userPersistencePort.findByEmail(TestConstants.USER_ONE_EMAIL))
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        // when
        val result = service.loadUserByUsername(TestConstants.USER_ONE_EMAIL)

        // then
        assertEquals(TestConstants.USER_ONE_EMAIL, result.username)
        assertEquals(TestConstants.USER_ONE_PASSWORD, result.password)
        assertTrue(result.authorities.any { it.authority == "ROLE_USER" })

        verify(userPersistencePort).findByEmail(TestConstants.USER_ONE_EMAIL)
    }
}
