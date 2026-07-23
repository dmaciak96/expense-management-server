package com.example.expense_management_server.adapter.api.application_user

import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserHttpResponse
import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserRegistrationHttpRequest
import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserUpdateHttpRequest
import com.example.expense_management_server.application.application_user.UserRegistrationUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/users")
class ApplicationUserController(
    private val userRegistrationUseCase: UserRegistrationUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerNewUser(@Valid @RequestBody registrationRequest: ApplicationUserRegistrationHttpRequest): ApplicationUserHttpResponse {
        val registeredUser = userRegistrationUseCase.execute(registrationRequest.toDomain())
        return ApplicationUserHttpResponse.fromDomain(registeredUser)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeCurrentLoginUser(): ResponseEntity<Void> {
        throw NotImplementedError()
    }

    @PutMapping("/{id}")
    fun updateUserData(
        @PathVariable id: UUID,
        @Valid @RequestBody updateRequest: ApplicationUserUpdateHttpRequest
    ): ResponseEntity<ApplicationUserHttpResponse> {
        throw NotImplementedError()
    }

    @GetMapping
    fun getCurrentLoginUser(): ResponseEntity<ApplicationUserHttpResponse> {
        throw NotImplementedError()
    }

    @GetMapping("/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<ApplicationUserHttpResponse> {
        throw NotImplementedError()
    }
}