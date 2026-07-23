package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import org.springframework.stereotype.Component

@Component
class PasswordValidator : UserValidationPort {

    override fun validate(user: ApplicationUser) {
        val password = user.password.trim()
        if (!LOWERCASE_PATTERN.containsMatchIn(password)) {
            throw UserValidationException("Password must contain at least 1 lowercase character")
        }
        if (!UPPERCASE_PATTERN.containsMatchIn(password)) {
            throw UserValidationException("Password must contain at least 1 uppercase character")
        }
        if (!DIGIT_PATTERN.containsMatchIn(password)) {
            throw UserValidationException("Password must contain at least 1 digit")
        }
        if (!SPECIAL_CHARACTER_PATTERN.containsMatchIn(password)) {
            throw UserValidationException("Password must contain at least 1 special character")
        }
    }

    companion object {
        private val LOWERCASE_PATTERN = Regex("[a-z]")
        private val UPPERCASE_PATTERN = Regex("[A-Z]")
        private val DIGIT_PATTERN = Regex("\\d")
        private val SPECIAL_CHARACTER_PATTERN = Regex("[^a-zA-Z0-9]")
    }
}