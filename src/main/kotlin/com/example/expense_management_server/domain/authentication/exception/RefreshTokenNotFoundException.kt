package com.example.expense_management_server.domain.authentication.exception

class RefreshTokenNotFoundException : RuntimeException("Refresh token not found") {
}