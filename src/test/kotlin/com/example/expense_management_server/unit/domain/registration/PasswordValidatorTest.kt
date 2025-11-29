package com.example.expense_management_server.unit.domain.registration

import com.example.expense_management_server.domain.user.PasswordValidationCriteria
import com.example.expense_management_server.domain.user.PasswordValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PasswordValidatorTest {

    private val passwordValidator = PasswordValidator()

    @Test
    fun `when password is too short then not meet criteria list should contain LENGTH`() {
        // given
        val password = "1aD@"

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).containsExactly(PasswordValidationCriteria.LENGTH)
    }

    @Test
    fun `when password not contain lower case then not meet criteria list should contain ONE_LOWER_CASE`() {
        // given
        val password = "1AD@DDDDDDDD"

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).containsExactly(PasswordValidationCriteria.ONE_LOWER_CASE)

    }

    @Test
    fun `when password not contain upper case then not meet criteria list should contain ONE_UPPER_CASE`() {
        // given
        val password = "1ad@dddddddd"

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).containsExactly(PasswordValidationCriteria.ONE_UPPER_CASE)

    }

    @Test
    fun `when password not contain number then not meet criteria list should contain ONE_NUMBER`() {
        // given
        val password = "aaD@DDDDDDDD"

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).containsExactly(PasswordValidationCriteria.ONE_NUMBER)
    }

    @Test
    fun `when password not contain special character then not meet criteria list should contain ONE_SPECIAL_CHARACTER`() {
        // given
        val password = "1aaDDDDDDDDD"

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).containsExactly(PasswordValidationCriteria.ONE_SPECIAL_CHARACTER)

    }

    @Test
    fun `when password contain space then not meet criteria list should contain NO_SPACE`() {
        // given
        val password = "1a@DDD DDDDDD "

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).containsExactly(PasswordValidationCriteria.NO_SPACE)
    }

    @Test
    fun `when password is empty then return all not meet criteria`() {
        // given
        val password = ""

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).containsExactlyInAnyOrder(*PasswordValidationCriteria.entries.toTypedArray())
    }

    @Test
    fun `when password meets all criteria then should return empty list`() {
        // given
        val password = "p@Ssw0rd"

        // when
        val result = passwordValidator.checkIfPasswordIsValid(password)

        // then
        assertThat(result).isEmpty()
    }
}