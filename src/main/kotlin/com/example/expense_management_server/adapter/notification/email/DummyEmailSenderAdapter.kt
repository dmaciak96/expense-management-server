package com.example.expense_management_server.adapter.notification.email

import com.example.expense_management_server.domain.user.port.EmailVerificationPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

// FIXME: That class was created only to fix missing bean error
@Component
class DummyEmailSenderAdapter : EmailVerificationPort {

    override fun sendEmailVerificationMessage(email: String) {
        LOGGER.info { "Dummy email sender adapter was run, email won't be send" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}