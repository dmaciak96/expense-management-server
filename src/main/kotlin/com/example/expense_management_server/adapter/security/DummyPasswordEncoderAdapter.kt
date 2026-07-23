package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.domain.application_user.port.PasswordEncoderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class DummyPasswordEncoderAdapter: PasswordEncoderPort {
    override fun encode(rawPassword: String): String {
        LOGGER.warn { "Dummy password encoder was used. Use this implementation only for testing" }
        return rawPassword
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}