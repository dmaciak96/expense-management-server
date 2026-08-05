package com.example.expense_management_server.adapter.api

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(Exception::class)
    fun handleUnknownException(
        exception: Exception
    ): ResponseEntity<ErrorResponse> {
        LOGGER.error(exception) { "Unhandled exception" }

        val status = HttpStatus.INTERNAL_SERVER_ERROR

        return ResponseEntity
            .status(status)
            .body(
                ErrorResponse(
                    message = "Unknown exception",
                    status = status.value()
                )
            )
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}