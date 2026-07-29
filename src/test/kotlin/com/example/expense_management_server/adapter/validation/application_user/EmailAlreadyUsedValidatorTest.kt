package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


class EmailAlreadyUsedValidatorTest {

    private var userPersistencePort: UserPersistencePort = mock()
    private var validator = EmailAlreadyUsedValidator(userPersistencePort)

    @Test
    fun `should accept email when user does not exist`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(email = "new-user@example.com")
        whenever(userPersistencePort.findByEmail(user.email)).thenThrow(UserNotFoundException("User not found"))

        assertDoesNotThrow {
            validator.validate(user)
        }

        verify(userPersistencePort).findByEmail(user.email)
    }

    @Test
    fun `should throw exception when email is already used`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(email = "existing-user@example.com")
        val existingUser = TestConstants.APPLICATION_USER_ONE.copy(email = "existing-user@example.com")
        whenever(userPersistencePort.findByEmail(user.email)).thenReturn(existingUser)

        val exception = assertThrows(UserAlreadyExistsException::class.java) {
            validator.validate(user)
        }

        assertEquals(
            "Email ${user.email} is already in use",
            exception.message
        )

        verify(userPersistencePort).findByEmail(user.email)
    }

    @Test
    fun `should propagate unexpected persistence exception`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(email = "user@example.com")

        whenever(userPersistencePort.findByEmail(user.email)).thenThrow(IllegalStateException("Database unavailable"))

        val exception = assertThrows(IllegalStateException::class.java) {
            validator.validate(user)
        }

        assertEquals("Database unavailable", exception.message)

        verify(userPersistencePort).findByEmail(user.email)
    }
}