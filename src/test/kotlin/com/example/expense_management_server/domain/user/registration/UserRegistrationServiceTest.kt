package com.example.expense_management_server.domain.user.registration

import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRegistrationDomainModel
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.domain.user.port.IEmailVerificationPort
import com.example.expense_management_server.domain.user.port.IPasswordEncoderPort
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import com.example.expense_management_server.domain.user.registration.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.registration.exception.UserAlreadyExistsException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserRegistrationServiceTest {

    @Mock
    private lateinit var userPersistencePort: IUserPersistencePort

    @Mock
    private lateinit var passwordEncoderPort: IPasswordEncoderPort

    @Mock
    private lateinit var emailVerificationPort: IEmailVerificationPort

    @Mock
    private lateinit var passwordValidator: PasswordValidator

    private lateinit var userRegistrationService: UserRegistrationService

    private val clock = Clock.fixed(
        Instant.parse("2018-04-29T10:15:30.00Z"),
        ZoneId.systemDefault()
    )

    @BeforeEach
    fun initialize() {
        userRegistrationService = UserRegistrationService(
            userPersistencePort,
            passwordEncoderPort,
            emailVerificationPort,
            passwordValidator,
            clock
        )
    }

    @Test
    fun `should register new user when email does not exist and password is valid`() {
        // given
        whenever(userPersistencePort.findUserAccountByEmail(REGISTRATION_MODEL.email))
            .thenReturn(null)

        whenever(passwordValidator.checkIfPasswordIsValid(REGISTRATION_MODEL.password))
            .thenReturn(emptyList())

        whenever(passwordEncoderPort.encodePassword(REGISTRATION_MODEL.password))
            .thenReturn(ENCODED_PASSWORD)

        val userToSave = UserDomainModel(
            id = null,
            email = REGISTRATION_MODEL.email,
            nickname = REGISTRATION_MODEL.nickname,
            passwordHash = ENCODED_PASSWORD,
            roles = listOf(UserRole.USER),
            isEmailVerified = false,
            createdAt = OffsetDateTime.now(clock),
            updatedAt = null,
            lastLoginAt = null,
            accountStatus = AccountStatus.ACTIVE
        )

        whenever(userPersistencePort.saveUserAccount(any()))
            .thenReturn(userToSave.copy(id = USER_ID_AFTER_SAVE))

        // when
        val result = userRegistrationService.registerNewUser(REGISTRATION_MODEL)

        // then
        assertThat(result).isEqualTo(userToSave.copy(id = USER_ID_AFTER_SAVE))

        val captor = argumentCaptor<UserDomainModel>()
        verify(userPersistencePort).saveUserAccount(captor.capture())
        val captured = captor.firstValue

        assertThat(captured.id).isNull()
        assertThat(captured.email).isEqualTo(REGISTRATION_MODEL.email)
        assertThat(captured.nickname).isEqualTo(REGISTRATION_MODEL.nickname)
        assertThat(captured.passwordHash).isEqualTo(ENCODED_PASSWORD)
        assertThat(captured.roles).containsExactly(UserRole.USER)
        assertThat(captured.isEmailVerified).isFalse()
        assertThat(captured.createdAt).isEqualTo(OffsetDateTime.now(clock))
        assertThat(captured.updatedAt).isNull()
        assertThat(captured.lastLoginAt).isNull()
        assertThat(captured.accountStatus).isEqualTo(AccountStatus.ACTIVE)

        verify(userPersistencePort).findUserAccountByEmail(REGISTRATION_MODEL.email)
        verify(passwordValidator).checkIfPasswordIsValid(REGISTRATION_MODEL.password)
        verify(passwordEncoderPort).encodePassword(REGISTRATION_MODEL.password)
        verify(emailVerificationPort).sendEmailVerificationMessage(userToSave.email)

        verifyNoMoreInteractions(
            userPersistencePort,
            passwordValidator,
            passwordEncoderPort,
            emailVerificationPort
        )
    }

    @Test
    fun `should throw UserAlreadyExistsException when user with email already exists`() {
        // given
        val existingUser = UserDomainModel(
            id = UUID.randomUUID(),
            email = REGISTRATION_MODEL.email,
            nickname = "existing",
            passwordHash = "hash",
            roles = listOf(UserRole.USER),
            isEmailVerified = true,
            createdAt = OffsetDateTime.now(),
            updatedAt = null,
            lastLoginAt = null,
            accountStatus = AccountStatus.ACTIVE
        )

        whenever(userPersistencePort.findUserAccountByEmail(REGISTRATION_MODEL.email))
            .thenReturn(existingUser)

        // when & then
        assertThrows<UserAlreadyExistsException> {
            userRegistrationService.registerNewUser(REGISTRATION_MODEL)
        }

        verify(userPersistencePort).findUserAccountByEmail(EMAIL)
        verifyNoInteractions(passwordValidator, passwordEncoderPort, emailVerificationPort)
    }

    @Test
    fun `should throw PasswordValidationException when password is invalid`() {
        // given
        whenever(userPersistencePort.findUserAccountByEmail(REGISTRATION_MODEL.email))
            .thenReturn(null)

        val validationErrors = listOf(PasswordValidationCriteria.ONE_LOWER_CASE)

        whenever(passwordValidator.checkIfPasswordIsValid(REGISTRATION_MODEL.password))
            .thenReturn(validationErrors)

        // when & then
        assertThrows<PasswordValidationException> {
            userRegistrationService.registerNewUser(REGISTRATION_MODEL)
        }

        verify(userPersistencePort).findUserAccountByEmail(REGISTRATION_MODEL.email)
        verify(passwordValidator).checkIfPasswordIsValid(REGISTRATION_MODEL.password)
        verifyNoInteractions(passwordEncoderPort, emailVerificationPort)
    }

    companion object {
        private const val EMAIL = "test@example.com"
        private const val NICKNAME = "tester"
        private const val PASSWORD = "StrongPassword123!"
        private const val ENCODED_PASSWORD = "encoded-password"

        private val REGISTRATION_MODEL = UserRegistrationDomainModel(
            email = EMAIL,
            nickname = NICKNAME,
            password = PASSWORD
        )

        private val USER_ID_AFTER_SAVE = UUID.randomUUID()
    }
}
