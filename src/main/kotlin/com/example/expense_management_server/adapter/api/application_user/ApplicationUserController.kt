package com.example.expense_management_server.adapter.api.application_user

import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserHttpResponse
import com.example.expense_management_server.adapter.api.application_user.model.ApplicationUserUpdateHttpRequest
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.FetchUserByEmailUseCase
import com.example.expense_management_server.application.application_user.RemoveCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.UpdateUserDataUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/users")
class ApplicationUserController(
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val fetchUserByEmailUseCase: FetchUserByEmailUseCase,
    private val removeCurrentLoginUserUseCase: RemoveCurrentLoginUserUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase
) {
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