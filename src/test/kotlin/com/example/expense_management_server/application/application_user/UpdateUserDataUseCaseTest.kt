package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.validation.application_user.EmailAlreadyUsedValidator
import com.example.expense_management_server.domain.application_user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import com.example.expense_management_server.domain.application_user.port.UserValidationPort
import com.example.expense_management_server.domain.authentication.port.PasswordEncoderPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class UpdateUserDataUseCaseTest {

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    @Mock
    private lateinit var passwordEncoderPort: PasswordEncoderPort

    @Mock
    private lateinit var userValidator: UserValidationPort

    @Mock
    private lateinit var secondUserValidator: UserValidationPort

    @Mock
    private lateinit var emailAlreadyUsedValidator: EmailAlreadyUsedValidator

    private lateinit var useCase: UpdateUserDataUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = UpdateUserDataUseCase(
            userDataValidators = listOf(
                userValidator,
                emailAlreadyUsedValidator,
                secondUserValidator
            ),
            userPersistencePort = userPersistencePort,
            passwordEncoderPort = passwordEncoderPort
        )
    }

    @Test
    fun `should update user data and encode password`() {
        // given
        val userId = UUID.randomUUID()

        val oldUser = TestConstants.APPLICATION_USER_ONE.copy(
            id = userId,
            email = "old@example.com",
            password = "old-password"
        )

        val newUserData = TestConstants.APPLICATION_USER_TWO.copy(
            id = userId,
        )

        val encodedPassword = "encoded-password"

        val savedUser = newUserData.copy(
            password = encodedPassword
        )

        whenever(userPersistencePort.findById(userId))
            .thenReturn(oldUser)

        whenever(userPersistencePort.findByEmail(newUserData.email))
            .thenThrow(UserNotFoundException("User not found"))

        whenever(passwordEncoderPort.encode(newUserData.password))
            .thenReturn(encodedPassword)

        whenever(userPersistencePort.update(any()))
            .thenReturn(savedUser)

        // when
        val result = useCase.execute(newUserData)

        // then
        assertSame(savedUser, result)

        val validatorCaptor = argumentCaptor<ApplicationUser>()

        verify(userValidator).validate(validatorCaptor.capture())
        verify(secondUserValidator).validate(any())

        verify(emailAlreadyUsedValidator, never()).validate(any())

        val validatedUser = validatorCaptor.firstValue

        assertEquals(userId, validatedUser.id)
        assertEquals(TestConstants.APPLICATION_USER_TWO.firstName, validatedUser.firstName)
        assertEquals(TestConstants.APPLICATION_USER_TWO.lastName, validatedUser.lastName)
        assertEquals(TestConstants.APPLICATION_USER_TWO.email, validatedUser.email)
        assertEquals(TestConstants.APPLICATION_USER_TWO.password, validatedUser.password)
        assertEquals(TestConstants.APPLICATION_USER_TWO.phoneNumber, validatedUser.phoneNumber)
        assertEquals(TestConstants.APPLICATION_USER_TWO.displayName, validatedUser.displayName)
        assertEquals(TestConstants.APPLICATION_USER_TWO.avatarUrl, validatedUser.avatarUrl)

        verify(passwordEncoderPort).encode(TestConstants.APPLICATION_USER_TWO.password)

        val updateCaptor = argumentCaptor<ApplicationUser>()
        verify(userPersistencePort).update(updateCaptor.capture())

        assertEquals(encodedPassword, updateCaptor.firstValue.password)
    }

    @Test
    fun `should allow keeping the same email`() {
        // given
        val userId = UUID.randomUUID()
        val email = "user@example.com"

        val oldUser = TestConstants.APPLICATION_USER_ONE.copy(
            id = userId,
            email = email
        )

        val newUserData = oldUser.copy(
            firstName = "Updated"
        )

        whenever(userPersistencePort.findById(userId))
            .thenReturn(oldUser)

        whenever(userPersistencePort.findByEmail(email))
            .thenReturn(oldUser)

        whenever(passwordEncoderPort.encode(newUserData.password))
            .thenReturn("encoded-password")

        whenever(userPersistencePort.update(any()))
            .thenAnswer { invocation ->
                invocation.getArgument(0)
            }

        // when
        useCase.execute(newUserData)

        // then
        verify(userPersistencePort).update(any())
    }

    @Test
    fun `should throw UserAlreadyExistsException when another user uses email`() {
        // given
        val updatingUserId = UUID.randomUUID()
        val email = "used@example.com"

        val oldUser = TestConstants.APPLICATION_USER_ONE.copy(
            id = updatingUserId,
            email = "old@example.com"
        )

        val newUserData = oldUser.copy(
            email = email
        )

        val anotherUser = TestConstants.APPLICATION_USER_TWO.copy(
            email = email
        )

        whenever(userPersistencePort.findById(updatingUserId))
            .thenReturn(oldUser)

        whenever(userPersistencePort.findByEmail(email))
            .thenReturn(anotherUser)

        // when
        val exception = assertThrows<UserAlreadyExistsException> {
            useCase.execute(newUserData)
        }

        // then
        assertEquals(
            "User with email $email already exists",
            exception.message
        )

        verify(userValidator, never()).validate(any())
        verify(secondUserValidator, never()).validate(any())
        verify(passwordEncoderPort, never()).encode(any())
        verify(userPersistencePort, never()).update(any())
    }

    @Test
    fun `should stop updating when validator throws exception`() {
        // given
        val userId = UUID.randomUUID()
        val user = TestConstants.APPLICATION_USER_ONE.copy(id = userId)
        val validationException = IllegalArgumentException("Invalid user data")

        whenever(userPersistencePort.findById(userId))
            .thenReturn(user)

        whenever(userPersistencePort.findByEmail(user.email))
            .thenReturn(user)

        whenever(userValidator.validate(any()))
            .thenThrow(validationException)

        // when
        val exception = assertThrows<IllegalArgumentException> {
            useCase.execute(user)
        }

        // then
        assertSame(validationException, exception)

        verify(passwordEncoderPort, never()).encode(any())
        verify(userPersistencePort, never()).update(any())
    }

    @Test
    fun `should propagate exception when user to update does not exist`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE.copy()

        whenever(userPersistencePort.findById(user.id))
            .thenThrow(UserNotFoundException("User not found"))

        // when
        assertThrows<UserNotFoundException> {
            useCase.execute(user)
        }

        // then
        verify(userPersistencePort, never()).findByEmail(any())
        verify(userValidator, never()).validate(any())
        verify(passwordEncoderPort, never()).encode(any())
        verify(userPersistencePort, never()).update(any())
    }
}