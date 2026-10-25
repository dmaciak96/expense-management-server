package com.example.expense_management_server.domain.application_user.exception

class VerificationTokenException(message: String = "Account verification failed", cause: Throwable? = null) :
    RuntimeException(message, cause) {
}