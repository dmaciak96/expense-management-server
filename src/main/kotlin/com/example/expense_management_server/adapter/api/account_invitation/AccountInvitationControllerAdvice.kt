package com.example.expense_management_server.adapter.api.account_invitation

import com.example.expense_management_server.adapter.api.ErrorResponse
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account_invitation.exception.InvitationExpiredException
import com.example.expense_management_server.domain.account_invitation.exception.InvitationNotFoundException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.exception.UserNotLoggedInException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [AccountInvitationController::class])
class AccountInvitationControllerAdvice {

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

    @ExceptionHandler(InvitationExpiredException::class)
    fun handleInvitationExpiredException(exception: InvitationExpiredException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvitationNotFoundException::class)
    fun handleInvitationNotFoundException(exception: InvitationNotFoundException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(exception: AccountNotFoundException): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
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