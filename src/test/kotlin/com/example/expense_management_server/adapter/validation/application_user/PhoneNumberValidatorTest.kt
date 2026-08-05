package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class PhoneNumberValidatorTest {

    private val validator = PhoneNumberValidator()

    @Test
    fun `should accept valid phone number`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            phoneNumber = "+48123123123"
        )

        // when & then
        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should accept null phone number`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            phoneNumber = null
        )

        // when & then
        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should accept blank phone number`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            phoneNumber = " "
        )

        // when & then
        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject invalid phone number`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            phoneNumber = "123-123-123"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }
}
