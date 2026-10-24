package com.example.expense_management_server.adapter.api.application_user

import com.example.expense_management_server.adapter.api.ErrorResponse
import com.example.expense_management_server.domain.application_user.exception.PasswordEncodingException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.exception.UserNotLoggedInException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [ApplicationUserController::class])
class ApplicationUserControllerAdvice {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        exception: UserNotFoundException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserNotLoggedInException::class)
    fun handleUserNotLoggedInException(
        exception: UserNotLoggedInException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(PasswordEncodingException::class)
    fun handlePasswordEncodingException(
        exception: PasswordEncodingException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR)
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
