package com.example.expense_management_server.adapter.api.expense

import com.example.expense_management_server.adapter.api.ErrorResponse
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.account.exception.ExpenseValidationException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.exception.UserNotLoggedInException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [ExpenseController::class])
class ExpenseControllerAdvice {

    @ExceptionHandler(AccountNotFoundException::class)
    fun handleAccountNotFoundException(
        exception: AccountNotFoundException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ExpenseNotFoundException::class)
    fun handleExpenseNotFoundException(
        exception: ExpenseNotFoundException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        exception: UserNotFoundException
    ): ResponseEntity<ErrorResponse> {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ExpenseValidationException::class)
    fun handleExpenseValidationException(
        exception: ExpenseValidationException
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
