package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class AvatarUrlValidatorTest {

    private val validator = AvatarUrlValidator()

    @Test
    fun `should accept valid avatar url`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            avatarUrl = "https://example.com/avatar.png"
        )

        // when & then
        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should accept null avatar url`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            avatarUrl = null
        )

        // when & then
        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject avatar url without http protocol`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            avatarUrl = "ftp://example.com/avatar.png"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject avatar url without host`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            avatarUrl = "https:///avatar.png"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }

    @Test
    fun `should reject avatar url not pointing to image`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy(
            avatarUrl = "https://example.com/avatar"
        )

        // when & then
        assertThrows<UserValidationException> {
            validator.validate(user)
        }
    }
}
