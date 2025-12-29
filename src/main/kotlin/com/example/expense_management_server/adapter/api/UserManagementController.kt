package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.adapter.api.model.UserRequest
import com.example.expense_management_server.adapter.api.model.UserResponse
import com.example.expense_management_server.domain.facade.IUserManagementFacade
import com.example.expense_management_server.domain.user.exception.NicknameValidationException
import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.UserHttpModel
import com.example.expense_management_server.domain.user.model.UserModel
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
    fun registerNewUser(@RequestBody @Valid userRequest: UserRequest): UserResponse {
        val registeredUser = userManagementFacade.registerNewUser(
            UserHttpModel(
                email = userRequest.email,
                password = userRequest.password,
                nickname = userRequest.nickname,
            )
        )

        LOGGER.debug { "Building HTTP response after successfully user registration" }
        return map(userModel = registeredUser)
    }

    @GetMapping("/{userId}")
    @PreAuthorize(IS_OWNER_OR_ADMIN_MATCHER)
    fun getUserDetails(@PathVariable userId: UUID): UserResponse {
        val user = userManagementFacade.getUserById(userId)
        return map(user)
    }

    @PutMapping("/{userId}")
    @PreAuthorize(IS_OWNER_OR_ADMIN_MATCHER)
    fun updateUserAccount(
        @PathVariable userId: UUID,
        @RequestBody @Valid userRequest: UserRequest
    ): UserResponse {
        LOGGER.info { "Update account request received" }
        val updatedUser = userManagementFacade.updateUser(
            userId, UserHttpModel(
                email = userRequest.email,
                password = userRequest.password,
                nickname = userRequest.nickname,
            )
        )
        return map(updatedUser)
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(IS_OWNER_OR_ADMIN_MATCHER)
    fun deleteUserAccount(
        @PathVariable userId: UUID,
    ) {
        LOGGER.info { "Remove account request received" }
        userManagementFacade.deleteUser(userId)
    }


    private fun map(userModel: UserModel) =
        UserResponse(
            id = userModel.id,
            email = userModel.email,
            nickname = userModel.nickname,
            emailVerified = userModel.isEmailVerified,
            createdAt = userModel.createdAt,
            updatedAt = userModel.updatedAt,
            lastLoginAt = userModel.lastLoginAt,
            accountStatus = userModel.accountStatus,
        )

    companion object {
        private val LOGGER = KotlinLogging.logger {}
        private const val IS_OWNER_OR_ADMIN_MATCHER = "#userId == principal.id || hasRole('ADMIN')"
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

    @ExceptionHandler(NicknameValidationException::class)
    fun handleNicknameValidationException(ex: NicknameValidationException): ResponseEntity<ProblemDetail> {
        LOGGER.warn { "Nickname validation error: ${ex.message}" }
        val error = ProblemDetail
            .forStatusAndDetail(HttpStatusCode.valueOf(400), ex.message)
        return ResponseEntity.badRequest()
            .body(error)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}

