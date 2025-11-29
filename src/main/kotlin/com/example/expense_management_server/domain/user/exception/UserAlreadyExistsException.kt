package com.example.expense_management_server.domain.user.exception

class UserAlreadyExistsException(email: String): RuntimeException("User with specified e-mail address $email already exists")