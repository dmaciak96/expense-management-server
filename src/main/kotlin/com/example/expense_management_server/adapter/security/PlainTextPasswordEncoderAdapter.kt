package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.domain.port.IPasswordEncoderPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

// FIXME: That class was created only to fix missing bean error, change implementation when you add spring-security starter
@Component
class PlainTextPasswordEncoderAdapter: IPasswordEncoderPort {

    override fun encodePassword(password: String): String {
        LOGGER.warn { "Plain text password encoder was run, implement real password encoder to secure application" }
        return password
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}