package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import org.springframework.stereotype.Component

@Component
class EmailAlreadyUsedValidator(
    private val userPersistencePort: UserPersistencePort
) : UserValidationPort {
    override fun validate(user: ApplicationUser) {
        try {
            // FindByEmail returns user or throws UserNotFoundException
            userPersistencePort.findByEmail(user.email)
            throw UserAlreadyExistsException("Email ${user.email} is already in use")
        } catch (e: UserNotFoundException) {
            return
        }
    }
}