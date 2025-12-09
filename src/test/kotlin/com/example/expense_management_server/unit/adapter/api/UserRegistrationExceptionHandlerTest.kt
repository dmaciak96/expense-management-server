package com.example.expense_management_server.unit.adapter.api

import com.example.expense_management_server.adapter.api.UserRegistrationExceptionHandler
import com.example.expense_management_server.domain.user.PasswordValidationCriteria
import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.exception.UserAlreadyExistsException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class UserRegistrationExceptionHandlerTest {

    private val handler = UserRegistrationExceptionHandler()

    @Test
    fun `when PasswordValidationException thrown then return bad request with problem detail`() {
        // given
        val exception = PasswordValidationException(
            listOf(PasswordValidationCriteria.ONE_LOWER_CASE)
        )

        // when
        val response = handler.handlePasswordValidationException(exception)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.detail).isEqualTo(exception.message)
    }

    @Test
    fun `when UserAlreadyExistsException thrown then return bad request with problem detail`() {
        // given
        val email = "already@exists.com"
        val exception = UserAlreadyExistsException(email)

        // when
        val response = handler.handleUserAlreadyExistsException(exception)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.detail).isEqualTo(exception.message)
    }
}
