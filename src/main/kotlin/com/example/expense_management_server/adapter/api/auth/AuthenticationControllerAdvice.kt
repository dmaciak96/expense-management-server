package com.example.expense_management_server.adapter.api.auth

import com.example.expense_management_server.adapter.api.ErrorResponse
import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.application_user.exception.UserValidationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [AuthenticationController::class])
class AuthenticationControllerAdvice {

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(
        exception: AuthenticationException
    ): ResponseEntity<ErrorResponse> = buildErrorResponse(exception, HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(
        exception: UserAlreadyExistsException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(UserValidationException::class)
    fun handleUserValidationException(
        exception: UserValidationException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST)
    }

    private fun buildErrorResponse(
        exception: Exception,
        status: HttpStatus
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(status)
            .body(
                ErrorResponse(
                    message = exception.message ?: status.reasonPhrase,
                    status = status.value()
                )
            )
    }
}
