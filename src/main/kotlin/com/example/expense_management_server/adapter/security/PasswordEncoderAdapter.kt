package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.domain.user.exception.PasswordEncodingException
import com.example.expense_management_server.domain.user.port.IPasswordEncoderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderAdapter(
    private val passwordEncoder: PasswordEncoder
) : IPasswordEncoderPort {

    override fun encodePassword(password: String): String {
        LOGGER.debug { "Encoding password with password encoder Spring Security bean" }
        val encodedPassword = passwordEncoder.encode(password)
        if (encodedPassword == null) {
            LOGGER.warn { "Spring Boot password encoder couldn't encode password properly" }
            throw PasswordEncodingException()
        }
        return encodedPassword
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}