package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import org.springframework.stereotype.Component

@Component
class PhoneNumberValidator : UserValidationPort {
    override fun validate(user: ApplicationUser) {
        if (user.phoneNumber.isNullOrBlank() || PHONE_PATTERN.matches(user.phoneNumber)) {
            return
        }
        throw UserValidationException("Invalid phone number")
    }

    companion object {
        private val PHONE_PATTERN = Regex("""^\+[1-9]\d{7,14}$""")
    }
}