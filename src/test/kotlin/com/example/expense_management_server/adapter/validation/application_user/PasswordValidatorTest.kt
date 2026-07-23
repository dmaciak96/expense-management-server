package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class PasswordValidatorTest {

    private val validator = PasswordValidator()

    @Test
    fun `should accept valid password`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "Password1!")

        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject password without lowercase character`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "PASSWORD1!")

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals(
            "Password must contain at least 1 lowercase character",
            exception.message
        )
    }

    @Test
    fun `should reject password without uppercase character`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "password1!")

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals(
            "Password must contain at least 1 uppercase character",
            exception.message
        )
    }

    @Test
    fun `should reject password without digit`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "Password!")

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals(
            "Password must contain at least 1 digit",
            exception.message
        )
    }

    @Test
    fun `should reject password without special character`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "Password1")

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals(
            "Password must contain at least 1 special character",
            exception.message
        )
    }

    @Test
    fun `should return first validation error when password violates multiple rules`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "123456")

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals(
            "Password must contain at least 1 lowercase character",
            exception.message
        )
    }

    @Test
    fun `should accept password surrounded by spaces when trimmed password is valid`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "  Password1!  ")

        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should treat space inside password as special character`() {
        val user = TestConstants.APPLICATION_USER_ONE.copy(password = "Password1 value")

        assertDoesNotThrow {
            validator.validate(user)
        }
    }
}