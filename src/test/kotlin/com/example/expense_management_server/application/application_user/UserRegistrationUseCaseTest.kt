package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.authentication.port.PasswordEncoderPort
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class UserRegistrationUseCaseTest {
    private val userValidationPortOne: UserValidationPort = mock()
    private val userValidationPortTwo: UserValidationPort = mock()
    private val userPersistencePort: UserPersistencePort = mock()
    private val passwordEncoderPort: PasswordEncoderPort = mock()

    private val userRegistrationUseCase = UserRegistrationUseCase(
        userDataValidators = listOf(userValidationPortOne, userValidationPortTwo),
        passwordEncoderPort = passwordEncoderPort,
        userPersistencePort = userPersistencePort
    )

    @Test
    fun `when user domain object is provided then should run all validators, encode password and save object`() {
        // given
        doNothing().`when`(userValidationPortOne).validate(TestConstants.APPLICATION_USER_ONE)
        doNothing().`when`(userValidationPortTwo).validate(TestConstants.APPLICATION_USER_ONE)
        `when`(userPersistencePort.create(TestConstants.APPLICATION_USER_ONE)).thenReturn(TestConstants.APPLICATION_USER_ONE)
        `when`(passwordEncoderPort.encode(TestConstants.USER_ONE_PASSWORD)).thenReturn(TestConstants.USER_ONE_PASSWORD)

        // when
        val result = userRegistrationUseCase.execute(TestConstants.APPLICATION_USER_ONE)

        // then
        assertThat(result, equalTo(TestConstants.APPLICATION_USER_ONE))
        verify(userValidationPortOne).validate(TestConstants.APPLICATION_USER_ONE)
        verify(userValidationPortTwo).validate(TestConstants.APPLICATION_USER_ONE)
        verify(passwordEncoderPort).encode(TestConstants.APPLICATION_USER_ONE.password)
        verify(userPersistencePort).create(TestConstants.APPLICATION_USER_ONE)
    }
}