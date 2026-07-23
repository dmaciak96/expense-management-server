package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.PasswordEncoderPort
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class UserRegistrationUseCase(
    private val userDataValidators: List<UserValidationPort>,
    private val userPersistencePort: UserPersistencePort,
    private val passwordEncoderPort: PasswordEncoderPort
) {

    fun execute(applicationUser: ApplicationUser): ApplicationUser {
        LOGGER.info { "Registering new user ${applicationUser.email}" }
        userDataValidators.forEach {
            LOGGER.debug { "Running ${it.javaClass.simpleName} to validate user data" }
            it.validate(applicationUser)
        }
        LOGGER.info { "User ${applicationUser.email} data are valid" }
        val encodedPassword = passwordEncoderPort.encode(applicationUser.password)
        LOGGER.debug { "Password encoded successfully" }
        val user = userPersistencePort.create(applicationUser.copy(password = encodedPassword))
        LOGGER.info { "User ${user.email} registered successfully" }
        return user
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}
