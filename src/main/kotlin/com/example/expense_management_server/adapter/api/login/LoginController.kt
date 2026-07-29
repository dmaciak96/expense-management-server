package com.example.expense_management_server.adapter.api.login

import com.example.expense_management_server.adapter.api.login.model.LoginHttpRequest
import com.example.expense_management_server.adapter.api.login.model.LoginHttpResponse
import com.example.expense_management_server.adapter.security.config.JwtProperties
import com.example.expense_management_server.application.application_user.UserLoginUseCase
import com.example.expense_management_server.domain.authentication.model.UserAuthentication
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController(
    private val userLoginUseCase: UserLoginUseCase,
    private val jwtProperties: JwtProperties
) {

    @PostMapping
    fun login(@Valid @RequestBody loginHttpRequest: LoginHttpRequest): LoginHttpResponse {
        val tokenValue = userLoginUseCase.execute(
            UserAuthentication(
                email = loginHttpRequest.email,
                password = loginHttpRequest.password
            )
        )
        return LoginHttpResponse(accessToken = tokenValue.token, expiresIn = jwtProperties.expiration)
    }
}