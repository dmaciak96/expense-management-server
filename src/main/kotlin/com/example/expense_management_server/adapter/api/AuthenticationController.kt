package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.adapter.api.model.AuthenticationRequest
import com.example.expense_management_server.adapter.api.model.AuthenticationResponse
import com.example.expense_management_server.domain.service.UserAuthenticationService
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class AuthenticationController(
    private val userAuthenticationService: UserAuthenticationService
) {

    @PostMapping
    fun authenticateUser(@RequestBody authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        val authentication =
            userAuthenticationService.authenticate(authenticationRequest.email, authenticationRequest.password)
        return AuthenticationResponse(authentication.token)
    }
}

@ControllerAdvice(assignableTypes = [AuthenticationController::class])
class AuthenticationExceptionHandler {

    @ExceptionHandler(BadCredentialsException::class, DisabledException::class, LockedException::class)
    fun handleAuthenticationException(ex: Exception): ResponseEntity<Void> {
        LOGGER.warn { "Authentication error: ${ex.message}" }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<Void> {
        LOGGER.warn { "Authentication error: ${ex.message}" }
        return ResponseEntity.notFound().build()
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}