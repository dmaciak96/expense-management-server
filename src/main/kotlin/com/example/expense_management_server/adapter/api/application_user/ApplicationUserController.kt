package com.example.expense_management_server.adapter.api.application_user

import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserHttpResponse
import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserRegistrationHttpRequest
import com.example.expense_management_server.application.application_user.ApplicationUserRegistrationUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class ApplicationUserController(
    private val applicationUserRegistrationUseCase: ApplicationUserRegistrationUseCase
) {

    @PostMapping
    fun registerNewUser(@Valid @RequestBody registrationRequest: ApplicationUserRegistrationHttpRequest): ResponseEntity<ApplicationUserHttpResponse> {
        throw NotImplementedError()
    }
}