package com.example.expense_management_server.domain.user

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class PasswordValidator {

    fun checkIfPasswordIsValid(password: String): List<PasswordValidationCriteria> {
        LOGGER.info { "Password validation in progress" }
        val criteriaNotMatched = PasswordValidationCriteria.entries
            .filter { it.isCriteriaNotMatched(password) }
        if (criteriaNotMatched.isNotEmpty()) {
            LOGGER.warn {
                "Password not meets the following criteria: ${
                    criteriaNotMatched.joinToString(
                        prefix = "[",
                        postfix = "]"
                    )
                }"
            }
        }
        return criteriaNotMatched
    }

    companion object {
        private val LOGGER = KotlinLogging.logger { }
    }
}

enum class PasswordValidationCriteria(val regex: Regex) {
    LENGTH(Regex("^.{8,}$")),
    ONE_UPPER_CASE(Regex("^(?=.*[A-Z]).+$")),
    ONE_LOWER_CASE(Regex("^(?=.*[a-z]).+$")),
    ONE_NUMBER(Regex("^(?=.*[0-9]).+$")),
    ONE_SPECIAL_CHARACTER(Regex("^(?=.*[@#$%^&+=]).+$")),
    NO_SPACE(Regex("^\\S+$"));

    fun isCriteriaNotMatched(text: String): Boolean = !text.matches(this.regex)
}