package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.domain.application_user.exception.PasswordEncodingException
import com.example.expense_management_server.domain.authentication.port.PasswordEncoderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderAdapter(
    private val passwordEncoder: PasswordEncoder
) : PasswordEncoderPort {
    override fun encode(rawPassword: String): String {
        LOGGER.debug { "Encoding password using ${this.javaClass.simpleName}" }
        val encodedPassword =
            passwordEncoder.encode(rawPassword) ?: throw PasswordEncodingException("Cannot encode the password")
        return encodedPassword
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}