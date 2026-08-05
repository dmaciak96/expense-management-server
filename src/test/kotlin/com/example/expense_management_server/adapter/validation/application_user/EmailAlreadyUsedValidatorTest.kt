package com.example.expense_management_server.adapter.validation.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class EmailAlreadyUsedValidatorTest {

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    private lateinit var validator: EmailAlreadyUsedValidator

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        validator = EmailAlreadyUsedValidator(
            userPersistencePort = userPersistencePort
        )
    }

    @Test
    fun `should accept email when user does not exist`() {
        // given
        whenever(userPersistencePort.findByEmail(TestConstants.USER_ONE_EMAIL))
            .thenThrow(UserNotFoundException("User not found"))

        // when & then
        assertDoesNotThrow {
            validator.validate(TestConstants.APPLICATION_USER_ONE)
        }

        verify(userPersistencePort).findByEmail(TestConstants.USER_ONE_EMAIL)
    }

    @Test
    fun `should reject email when user already exists`() {
        // given
        whenever(userPersistencePort.findByEmail(TestConstants.USER_ONE_EMAIL))
            .thenReturn(TestConstants.APPLICATION_USER_ONE)

        // when & then
        assertThrows<UserAlreadyExistsException> {
            validator.validate(TestConstants.APPLICATION_USER_ONE)
        }

        verify(userPersistencePort).findByEmail(TestConstants.USER_ONE_EMAIL)
    }
}
