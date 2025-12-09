package com.example.expense_management_server.domain.user.exception

import com.example.expense_management_server.domain.user.PasswordValidationCriteria

class PasswordValidationException(passwordValidationErrors: List<PasswordValidationCriteria>) : RuntimeException(
    "Password not meet requirements: ${
        passwordValidationErrors.joinToString(
            prefix = "[",
            postfix = "]"
        )
    }"
)
