package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class PasswordValidatorTest {

    private val validator = PasswordValidator()

    @Test
    fun `should accept valid password`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            password = "ValidPassword1!"
        )

        // when & then
        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject password without lowercase character`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            password = "PASSWORD1!"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject password without uppercase character`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            password = "password1!"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject password without digit`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            password = "Password!"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject password without special character`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            password = "Password1"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }
}
