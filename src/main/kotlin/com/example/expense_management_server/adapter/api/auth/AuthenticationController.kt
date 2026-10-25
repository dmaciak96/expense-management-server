package com.example.expense_management_server.adapter.api.auth

import com.example.expense_management_server.adapter.api.auth.model.LoginHttpRequest
import com.example.expense_management_server.adapter.api.auth.model.LoginHttpResponse
import com.example.expense_management_server.adapter.api.auth.model.RegistrationHttpRequest
import com.example.expense_management_server.adapter.api.auth.model.RegistrationHttpResponse
import com.example.expense_management_server.adapter.security.config.JwtProperties
import com.example.expense_management_server.application.application_user.UserAccountVerificationUseCase
import com.example.expense_management_server.application.application_user.UserLoginUseCase
import com.example.expense_management_server.application.application_user.UserRegistrationUseCase
import com.example.expense_management_server.domain.authentication.model.UserAuthentication
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    private val userLoginUseCase: UserLoginUseCase,
    private val userRegistrationUseCase: UserRegistrationUseCase,
    private val userAccountVerificationUseCase: UserAccountVerificationUseCase,
    private val jwtProperties: JwtProperties
) {

    @PostMapping("login")
    fun login(@Valid @RequestBody loginHttpRequest: LoginHttpRequest): LoginHttpResponse {
        val tokenValue = userLoginUseCase.execute(
            UserAuthentication(
                email = loginHttpRequest.email,
                password = loginHttpRequest.password
            )
        )
        return LoginHttpResponse(accessToken = tokenValue.token, expiresIn = jwtProperties.expirationInSeconds)
    }

    @PostMapping("/logout")
    fun logout() {
        throw NotImplementedError()
    }

    @PostMapping("/token/refresh")
    fun refreshAccessToken() {
        throw NotImplementedError()
    }

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerNewUser(@Valid @RequestBody registrationRequest: RegistrationHttpRequest): RegistrationHttpResponse {
        val registeredUser = userRegistrationUseCase.execute(registrationRequest.toDomain())
        return RegistrationHttpResponse.fromDomain(registeredUser)
    }

    @GetMapping("/email/verify")
    fun verifyEmail(@RequestParam token: String) {
        userAccountVerificationUseCase.execute(token)
    }

    @PostMapping("/password/forgot")
    fun sendPasswordResetMessage() {
        throw NotImplementedError()
    }

    @PostMapping("/password/reset")
    fun resetPassword() {
        throw NotImplementedError()
    }

    @PostMapping("/mfa/enroll")
    fun enrollMfa() {
        throw NotImplementedError()
    }

    @PostMapping("/mfa/replace")
    fun replaceMfa() {
        throw NotImplementedError()
    }

    @PostMapping("/mfa/confirm")
    fun confirmMfa() {
        throw NotImplementedError()
    }

    @PostMapping("/mfa/verify")
    fun verifyMfa() {
        throw NotImplementedError()
    }
}