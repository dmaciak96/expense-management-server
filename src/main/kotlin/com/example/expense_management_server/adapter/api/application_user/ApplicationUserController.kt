package com.example.expense_management_server.adapter.api.application_user

import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserHttpResponse
import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserRegistrationHttpRequest
import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserUpdateHttpRequest
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.application.application_user.RemoveCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.UpdateUserDataUseCase
import com.example.expense_management_server.application.application_user.UserRegistrationUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
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
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val fetchUserByEmailUseCase: FetchUserByEmailUseCase,
    private val userRegistrationUseCase: UserRegistrationUseCase,
    private val removeCurrentLoginUserUseCase: RemoveCurrentLoginUserUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerNewUser(@Valid @RequestBody registrationRequest: ApplicationUserRegistrationHttpRequest): ApplicationUserHttpResponse {
        val registeredUser = userRegistrationUseCase.execute(registrationRequest.toDomain())
        return ApplicationUserHttpResponse.fromDomain(registeredUser)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeCurrentLoginUser() {
        removeCurrentLoginUserUseCase.execute()
    }

    @PutMapping("/{id}")
    fun updateUserData(
        @PathVariable id: UUID,
        @Valid @RequestBody updateRequest: ApplicationUserUpdateHttpRequest
    ): ApplicationUserHttpResponse {
        val updatedUser = updateUserDataUseCase.execute(updateRequest.toDomain().copy(id = id))
        return ApplicationUserHttpResponse.fromDomain(updatedUser)
    }

    @GetMapping
    fun getCurrentLoginUser(): ApplicationUserHttpResponse {
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        return ApplicationUserHttpResponse.fromDomain(currentUser)
    }

    @GetMapping("/{email}")
    fun getUserByEmail(@PathVariable email: String): ApplicationUserHttpResponse {
        val user = fetchUserByEmailUseCase.execute(email)
        return ApplicationUserHttpResponse.fromDomain(user)
    }
}