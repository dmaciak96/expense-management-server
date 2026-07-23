package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URISyntaxException

@Component
class AvatarUrlValidator : UserValidationPort {
    override fun validate(user: ApplicationUser) {
        if (user.avatarUrl.isNullOrBlank()) {
            return
        }
        try {
            val uri = URI(user.avatarUrl)
            if (uri.scheme !in setOf("http", "https")) {
                throw UserValidationException("Missing http or https protocol")
            }

            if (uri.host.isNullOrBlank()) {
                throw UserValidationException("Missing host")
            }

            if (!IMAGE_EXTENSION_PATTERN.matches(user.avatarUrl)) {
                throw UserValidationException("Url not pointing to the image")
            }
        } catch (e: URISyntaxException) {
            throw UserValidationException("Invalid avatar url: ${e.message}")
        }
    }

    companion object {
        private val IMAGE_EXTENSION_PATTERN =
            Regex(""".*\.(jpg|jpeg|png|gif|webp|bmp|svg)(\?.*)?$""", RegexOption.IGNORE_CASE)
    }
}