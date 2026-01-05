package com.example.expense_management_server.domain.user

import com.example.expense_management_server.domain.service.UserAuthenticationService
import com.example.expense_management_server.domain.user.model.UserAuthenticationModel
import com.example.expense_management_server.domain.user.port.UserAuthenticationPort
import com.example.expense_management_server.domain.user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class UserAuthenticationServiceImpl(
    private val userAuthenticationPort: UserAuthenticationPort,
    private val userPersistencePort: UserPersistencePort,
) : UserAuthenticationService {
    override fun authenticate(email: String, password: String): UserAuthenticationModel {
        LOGGER.info { "Try to authenticate user $email" }
        val token = userAuthenticationPort.authenticateAndGenerateJwtToken(email, password)
        return UserAuthenticationModel(token)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}