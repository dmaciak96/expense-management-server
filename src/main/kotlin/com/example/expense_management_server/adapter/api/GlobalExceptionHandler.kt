package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PasswordValidationException::class)
    fun handlePasswordValidationException(ex: PasswordValidationException): ResponseEntity<ProblemDetail> {
        LOGGER.error { "Unknown API error: ${ex.message}" }
        val error = ProblemDetail
            .forStatusAndDetail(HttpStatusCode.valueOf(500), "Unknown API error: ${ex.message}")
        return ResponseEntity.badRequest()
            .body(error)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}