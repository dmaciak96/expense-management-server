package com.example.expense_management_server.adapter.api.account_members

import com.example.expense_management_server.adapter.api.ErrorResponse
import com.example.expense_management_server.domain.account.exception.AccountMemberNotFoundException
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.exception.AccountValidationException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.exception.UserNotLoggedInException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [AccountMemberController::class])
class AccountMemberControllerAdvice {

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(
        exception: AccountNotFoundException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AccountMemberNotFoundException::class)
    fun handleAccountMemberNotFoundException(
        exception: AccountMemberNotFoundException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        exception: UserNotFoundException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AccountValidationException::class)
    fun handleAccountValidationException(
        exception: AccountValidationException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserNotLoggedInException::class)
    fun handleUserNotLoggedInException(
        exception: UserNotLoggedInException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.UNAUTHORIZED)
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
