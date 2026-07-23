package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

class AvatarUrlValidatorTest {
    private val validator = AvatarUrlValidator()

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = ["", " ", "   "])
    fun `should accept missing avatar url`(avatarUrl: String?) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(avatarUrl = avatarUrl)

        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://example.com/avatar.jpg",
            "http://example.com/avatar.jpeg",
            "https://example.com/images/avatar.png",
            "https://cdn.example.com/user/avatar.gif",
            "https://example.com/avatar.webp"
        ]
    )
    fun `should accept valid image url`(avatarUrl: String) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(avatarUrl = avatarUrl)

        assertDoesNotThrow {
            validator.validate(user)
        }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "ftp://example.com/avatar.jpg",
            "file://example.com/avatar.jpg",
            "mailto:user@example.com"
        ]
    )
    fun `should reject url with unsupported protocol`(avatarUrl: String) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(avatarUrl = avatarUrl)

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals("Missing http or https protocol", exception.message)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https:///avatar.jpg",
            "http:///image.png"
        ]
    )
    fun `should reject url without host`(avatarUrl: String) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(avatarUrl = avatarUrl)

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals("Missing host", exception.message)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://example.com/avatar.pdf",
            "https://example.com/avatar.txt",
            "https://example.com/avatar",
        ]
    )
    fun `should reject url not ending with supported image extension`(avatarUrl: String) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(avatarUrl = avatarUrl)

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assertEquals("Url not pointing to the image", exception.message)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "https://exam ple.com/avatar.jpg",
            "https://[invalid/avatar.jpg",
            "https://example.com/avatar image.jpg"
        ]
    )
    fun `should reject syntactically invalid url`(avatarUrl: String) {
        val user = TestConstants.APPLICATION_USER_ONE.copy(avatarUrl = avatarUrl)

        val exception = assertThrows(UserValidationException::class.java) {
            validator.validate(user)
        }

        assert(exception.message?.startsWith("Invalid avatar url:") == true)
    }
}