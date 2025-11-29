package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.adapter.api.model.UserRegistrationRequest
import com.example.expense_management_server.adapter.api.model.UserResponse
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRegistrationDomainModel
import com.example.expense_management_server.domain.facade.IUserManagementFacade
import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserManagementController(
    private val userManagementFacade: IUserManagementFacade
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerNewUser(@RequestBody @Valid userRegistrationRequest: UserRegistrationRequest): UserResponse {
        val registeredUser = userManagementFacade.registerNewUser(
            UserRegistrationDomainModel(
                email = userRegistrationRequest.email,
                password = userRegistrationRequest.password,
                nickname = userRegistrationRequest.nickname,
            )
        )

        LOGGER.debug { "Building HTTP response after successfully user registration" }
        return map(userDomainModel = registeredUser)
    }

    @GetMapping("/{userId}")
    fun getUserDetails(@PathVariable userId: UUID): UserResponse {
        val user = userManagementFacade.getUserById(userId)
        // TODO: Check authenticated user before return
        return map(user)
    }


    private fun map(userDomainModel: UserDomainModel) =
        UserResponse(
            id = userDomainModel.id,
            email = userDomainModel.email,
            nickname = userDomainModel.nickname,
            isEmailVerified = userDomainModel.isEmailVerified,
            createdAt = userDomainModel.createdAt,
            updatedAt = userDomainModel.updatedAt,
            lastLoginAt = userDomainModel.lastLoginAt,
            accountStatus = userDomainModel.accountStatus,
        )

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}

@ControllerAdvice(assignableTypes = [UserManagementController::class])
class UserRegistrationExceptionHandler {

    @ExceptionHandler(PasswordValidationException::class)
    fun handlePasswordValidationException(ex: PasswordValidationException): ResponseEntity<ProblemDetail> {
        LOGGER.warn { "Password validation error: ${ex.message}" }
        val error = ProblemDetail
            .forStatusAndDetail(HttpStatusCode.valueOf(400), ex.message)
        return ResponseEntity.badRequest()
            .body(error)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<ProblemDetail> {
        val error = ProblemDetail
            .forStatusAndDetail(HttpStatusCode.valueOf(400), ex.message)
        return ResponseEntity.badRequest()
            .body(error)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity.HeadersBuilder<*> {
        LOGGER.warn { "User not found by e-mail" }
        return ResponseEntity.notFound()
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}

