package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

class PhoneNumberValidatorTest {

    private val validator = PhoneNumberValidator()

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ["", " ", "   "])
    fun `should accept missing phone number`(phoneNumber: String?) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(phoneNumber = phoneNumber)

        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "+48123456789",
            "+12345678",
            "+123456789012345"
        ]
    )
    fun `should accept valid phone number`(phoneNumber: String) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(phoneNumber = phoneNumber)

        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "48123456789",
            "123456789",
            "+012345678",
            "+1234567",
            "+1234567890123456",
            "+48 123 456 789",
            "+48-123-456-789",
            "+48abc123456",
            "++48123456789"
        ]
    )
    fun `should reject invalid phone number`(phoneNumber: String) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(phoneNumber = phoneNumber)

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals("Invalid phone number", exception.message)
    }
}