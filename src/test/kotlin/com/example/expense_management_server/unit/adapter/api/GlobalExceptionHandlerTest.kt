package com.example.expense_management_server.unit.adapter.api

import com.example.expense_management_server.adapter.api.GlobalExceptionHandler
import com.example.expense_management_server.domain.user.PasswordValidationCriteria
import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `when PasswordValidationException handled globally then return bad request with generic problem detail`() {
        // given
        val validationErrors = listOf(PasswordValidationCriteria.ONE_LOWER_CASE)
        val exception = PasswordValidationException(validationErrors)

        // when
        val response = handler.handlePasswordValidationException(exception)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        val body = response.body
        assertThat(body).isNotNull
        assertThat(body!!.status).isEqualTo(500)
        assertThat(body.detail).isEqualTo("Unknown API error: ${exception.message}")
    }
}
