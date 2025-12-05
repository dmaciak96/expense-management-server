package com.example.expense_management_server.unit.domain.user

import com.example.expense_management_server.domain.port.IEmailVerificationPort
import com.example.expense_management_server.domain.port.IPasswordEncoderPort
import com.example.expense_management_server.domain.port.IUserPersistencePort
import com.example.expense_management_server.domain.user.PasswordValidationCriteria
import com.example.expense_management_server.domain.user.PasswordValidator
import com.example.expense_management_server.domain.user.UserManagementService
import com.example.expense_management_server.domain.user.exception.NicknameValidationException
import com.example.expense_management_server.domain.user.exception.PasswordValidationException
import com.example.expense_management_server.domain.user.exception.UserAlreadyExistsException
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserHttpDomainModel
import com.example.expense_management_server.domain.user.model.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserManagementServiceTest {

    @Mock
    private lateinit var userPersistencePort: IUserPersistencePort

    @Mock
    private lateinit var passwordEncoderPort: IPasswordEncoderPort

    @Mock
    private lateinit var emailVerificationPort: IEmailVerificationPort

    @Mock
    private lateinit var passwordValidator: PasswordValidator

    private lateinit var userManagementService: UserManagementService

    private val clock = Clock.fixed(
        Instant.parse("2018-04-29T10:15:30.00Z"),
        ZoneId.systemDefault()
    )

    @BeforeEach
    fun initialize() {
        userManagementService = UserManagementService(
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

        whenever(passwordValidator.validate(REGISTRATION_MODEL.password))
            .thenReturn(emptyList())

        whenever(passwordEncoderPort.encodePassword(REGISTRATION_MODEL.password))
            .thenReturn(ENCODED_PASSWORD)

        whenever(userPersistencePort.saveOrUpdateUserAccount(any()))
            .thenReturn(USER_ACCOUNT_MODEL)

        // when
        val result = userManagementService.registerNewUser(REGISTRATION_MODEL)

        // then
        assertThat(result).isEqualTo(USER_ACCOUNT_MODEL)

        verify(userPersistencePort).findUserAccountByEmail(REGISTRATION_MODEL.email)
        verify(passwordValidator).validate(REGISTRATION_MODEL.password)
        verify(passwordEncoderPort).encodePassword(REGISTRATION_MODEL.password)
        verify(emailVerificationPort).sendEmailVerificationMessage(USER_ACCOUNT_MODEL.email)

        verifyNoMoreInteractions(
            userPersistencePort,
            passwordValidator,
            passwordEncoderPort,
            emailVerificationPort
        )
    }

    @Test
    fun `should register new user when email does not exist, password is valid and nickname is null`() {
        // given
        val accountModelWithoutNickname = USER_ACCOUNT_MODEL.copy(nickname = null)
        val registrationModelWithoutNickname = REGISTRATION_MODEL.copy(nickname = null)
        whenever(userPersistencePort.findUserAccountByEmail(registrationModelWithoutNickname.email))
            .thenReturn(null)

        whenever(passwordValidator.validate(registrationModelWithoutNickname.password))
            .thenReturn(emptyList())

        whenever(passwordEncoderPort.encodePassword(registrationModelWithoutNickname.password))
            .thenReturn(ENCODED_PASSWORD)

        whenever(userPersistencePort.saveOrUpdateUserAccount(any()))
            .thenReturn(accountModelWithoutNickname)

        // when
        val result = userManagementService.registerNewUser(registrationModelWithoutNickname)

        // then
        assertThat(result).isEqualTo(accountModelWithoutNickname)

        verify(userPersistencePort).findUserAccountByEmail(registrationModelWithoutNickname.email)
        verify(passwordValidator).validate(registrationModelWithoutNickname.password)
        verify(passwordEncoderPort).encodePassword(registrationModelWithoutNickname.password)
        verify(emailVerificationPort).sendEmailVerificationMessage(accountModelWithoutNickname.email)

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
        whenever(userPersistencePort.findUserAccountByEmail(REGISTRATION_MODEL.email))
            .thenReturn(USER_ACCOUNT_MODEL)

        // when & then
        assertThrows<UserAlreadyExistsException> {
            userManagementService.registerNewUser(REGISTRATION_MODEL)
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

        whenever(passwordValidator.validate(REGISTRATION_MODEL.password))
            .thenReturn(validationErrors)

        // when & then
        assertThrows<PasswordValidationException> {
            userManagementService.registerNewUser(REGISTRATION_MODEL)
        }

        verify(userPersistencePort).findUserAccountByEmail(REGISTRATION_MODEL.email)
        verify(passwordValidator).validate(REGISTRATION_MODEL.password)
        verifyNoInteractions(passwordEncoderPort, emailVerificationPort)
    }

    @Test
    fun `should throw NicknameValidationException when nickname is empty or has only whitespaces`() {
        // given
        whenever(userPersistencePort.findUserAccountByEmail(REGISTRATION_MODEL.email))
            .thenReturn(null)

        whenever(passwordValidator.validate(REGISTRATION_MODEL.password))
            .thenReturn(emptyList())

        whenever(passwordEncoderPort.encodePassword(REGISTRATION_MODEL.password))
            .thenReturn(ENCODED_PASSWORD)

        // when & then
        assertThrows<NicknameValidationException> {
            userManagementService.registerNewUser(REGISTRATION_MODEL.copy(nickname = ""))
        }

        assertThrows<NicknameValidationException> {
            userManagementService.registerNewUser(REGISTRATION_MODEL.copy(nickname = "   "))
        }
    }

    @Test
    fun `should throws UserNotFoundException when user with specified email not found`() {
        // given
        whenever(userPersistencePort.findUserAccountByEmail(NOT_EXISTING_EMAIL))
            .thenReturn(null)

        // when & then
        assertThrows<UserNotFoundException> {
            userManagementService.getUserByEmail(NOT_EXISTING_EMAIL)
        }
    }

    @Test
    fun `should return proper user account when user with specified email exists`() {
        // given
        whenever(userPersistencePort.findUserAccountByEmail(EMAIL))
            .thenReturn(USER_ACCOUNT_MODEL)

        // when
        val result = userManagementService.getUserByEmail(EMAIL)

        // then
        assertThat(result).isEqualTo(USER_ACCOUNT_MODEL)
    }

    @Test
    fun `should throws UserNotFoundException when user with specified id not found`() {
        // given
        whenever(userPersistencePort.findUserAccountById(NOT_EXISTING_USER_ID))
            .thenReturn(null)

        // when & then
        assertThrows<UserNotFoundException> {
            userManagementService.getUserById(NOT_EXISTING_USER_ID)
        }
    }

    @Test
    fun `should return proper user account when user with specified id exists`() {
        // given
        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(USER_ACCOUNT_MODEL)

        // when
        val result = userManagementService.getUserById(USER_ID)

        // then
        assertThat(result).isEqualTo(USER_ACCOUNT_MODEL)
    }

    @Test
    fun `should update user when email and nickname are unchanged`() {
        // given
        val existingUser = USER_ACCOUNT_MODEL.copy(
            email = EMAIL,
            nickname = NICKNAME,
            isEmailVerified = true,
            updatedAt = null
        )

        val updateModel = UserHttpDomainModel(
            email = EMAIL,
            nickname = NICKNAME,
            password = PASSWORD
        )

        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(existingUser)

        whenever(passwordValidator.validate(PASSWORD))
            .thenReturn(emptyList())

        whenever(passwordEncoderPort.encodePassword(PASSWORD))
            .thenReturn(ENCODED_PASSWORD)

        val expectedUpdatedAt = OffsetDateTime.ofInstant(clock.instant(), clock.zone)

        val savedUser = existingUser.copy(
            passwordHash = ENCODED_PASSWORD,
            updatedAt = expectedUpdatedAt
        )

        whenever(userPersistencePort.saveOrUpdateUserAccount(any()))
            .thenReturn(savedUser)

        // when
        val result = userManagementService.updateUser(USER_ID, updateModel)

        // then
        assertThat(result).isEqualTo(savedUser)

        val captor = argumentCaptor<UserDomainModel>()
        verify(userPersistencePort).findUserAccountById(USER_ID)
        verify(passwordValidator).validate(PASSWORD)
        verify(passwordEncoderPort).encodePassword(PASSWORD)
        verify(userPersistencePort).saveOrUpdateUserAccount(captor.capture())

        val savedArgument = captor.firstValue
        assertThat(savedArgument.id).isEqualTo(USER_ID)
        assertThat(savedArgument.email).isEqualTo(EMAIL)
        assertThat(savedArgument.nickname).isEqualTo(NICKNAME)
        assertThat(savedArgument.isEmailVerified).isTrue()
        assertThat(savedArgument.updatedAt).isEqualTo(expectedUpdatedAt)

        verifyNoInteractions(emailVerificationPort)
        verifyNoMoreInteractions(userPersistencePort, passwordValidator, passwordEncoderPort)
    }

    @Test
    fun `should update user and send verification email when email changed`() {
        // given
        val existingUser = USER_ACCOUNT_MODEL.copy(
            email = EMAIL,
            nickname = NICKNAME,
            isEmailVerified = true,
            updatedAt = null
        )

        val updateModel = UserHttpDomainModel(
            email = NEW_EMAIL,
            nickname = NEW_NICKNAME,
            password = PASSWORD
        )

        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(existingUser)

        whenever(userPersistencePort.findUserAccountByEmail(NEW_EMAIL))
            .thenReturn(null)

        whenever(passwordValidator.validate(PASSWORD))
            .thenReturn(emptyList())

        whenever(passwordEncoderPort.encodePassword(PASSWORD))
            .thenReturn(ENCODED_PASSWORD)

        val expectedUpdatedAt = OffsetDateTime.ofInstant(clock.instant(), clock.zone)

        val savedUser = existingUser.copy(
            email = NEW_EMAIL,
            nickname = NEW_NICKNAME,
            passwordHash = ENCODED_PASSWORD,
            isEmailVerified = false,
            updatedAt = expectedUpdatedAt
        )

        whenever(userPersistencePort.saveOrUpdateUserAccount(any()))
            .thenReturn(savedUser)

        // when
        val result = userManagementService.updateUser(USER_ID, updateModel)

        // then
        assertThat(result).isEqualTo(savedUser)

        val captor = argumentCaptor<UserDomainModel>()
        verify(userPersistencePort).findUserAccountById(USER_ID)
        verify(userPersistencePort).findUserAccountByEmail(NEW_EMAIL)
        verify(passwordValidator).validate(PASSWORD)
        verify(passwordEncoderPort).encodePassword(PASSWORD)
        verify(userPersistencePort).saveOrUpdateUserAccount(captor.capture())
        verify(emailVerificationPort).sendEmailVerificationMessage(NEW_EMAIL)

        val savedArgument = captor.firstValue
        assertThat(savedArgument.email).isEqualTo(NEW_EMAIL)
        assertThat(savedArgument.nickname).isEqualTo(NEW_NICKNAME)
        assertThat(savedArgument.isEmailVerified).isFalse()
        assertThat(savedArgument.updatedAt).isEqualTo(expectedUpdatedAt)

        verifyNoMoreInteractions(userPersistencePort, passwordValidator, passwordEncoderPort, emailVerificationPort)
    }

    @Test
    fun `should throw UserNotFoundException when updating non existing user`() {
        // given
        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(null)

        val updateModel = UserHttpDomainModel(
            email = EMAIL,
            nickname = NICKNAME,
            password = PASSWORD
        )

        // when & then
        assertThrows<UserNotFoundException> {
            userManagementService.updateUser(USER_ID, updateModel)
        }

        verify(userPersistencePort).findUserAccountById(USER_ID)
        verifyNoInteractions(passwordValidator, passwordEncoderPort, emailVerificationPort)
        verifyNoMoreInteractions(userPersistencePort)
    }

    @Test
    fun `should throw UserAlreadyExistsException when new email already in use`() {
        // given
        val existingUser = USER_ACCOUNT_MODEL.copy(
            email = EMAIL,
            nickname = NICKNAME
        )

        val updateModel = UserHttpDomainModel(
            email = NEW_EMAIL,
            nickname = NEW_NICKNAME,
            password = PASSWORD
        )

        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(existingUser)

        whenever(userPersistencePort.findUserAccountByEmail(NEW_EMAIL))
            .thenReturn(USER_ACCOUNT_MODEL)

        // when & then
        assertThrows<UserAlreadyExistsException> {
            userManagementService.updateUser(USER_ID, updateModel)
        }

        verify(userPersistencePort).findUserAccountById(USER_ID)
        verify(userPersistencePort).findUserAccountByEmail(NEW_EMAIL)
        verifyNoInteractions(passwordValidator, passwordEncoderPort, emailVerificationPort)
        verifyNoMoreInteractions(userPersistencePort)
    }

    @Test
    fun `should throw PasswordValidationException when updating with invalid password`() {
        // given
        val existingUser = USER_ACCOUNT_MODEL.copy(
            email = EMAIL,
            nickname = NICKNAME
        )

        val updateModel = UserHttpDomainModel(
            email = EMAIL,          // email bez zmiany
            nickname = NICKNAME,
            password = PASSWORD
        )

        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(existingUser)

        val validationErrors = listOf(PasswordValidationCriteria.ONE_LOWER_CASE)

        whenever(passwordValidator.validate(PASSWORD))
            .thenReturn(validationErrors)

        // when & then
        assertThrows<PasswordValidationException> {
            userManagementService.updateUser(USER_ID, updateModel)
        }

        verify(userPersistencePort).findUserAccountById(USER_ID)
        verify(passwordValidator).validate(PASSWORD)
        verifyNoInteractions(passwordEncoderPort, emailVerificationPort)
        verifyNoMoreInteractions(userPersistencePort, passwordValidator)
    }

    @Test
    fun `should allow clearing nickname by setting it to null`() {
        // given
        val existingUser = USER_ACCOUNT_MODEL.copy(
            email = EMAIL,
            nickname = NICKNAME
        )

        val updateModel = UserHttpDomainModel(
            email = EMAIL,
            nickname = null,
            password = PASSWORD
        )

        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(existingUser)

        whenever(passwordValidator.validate(PASSWORD))
            .thenReturn(emptyList())

        whenever(passwordEncoderPort.encodePassword(PASSWORD))
            .thenReturn(ENCODED_PASSWORD)

        val expectedUpdatedAt = OffsetDateTime.ofInstant(clock.instant(), clock.zone)

        val savedUser = existingUser.copy(
            nickname = null,
            passwordHash = ENCODED_PASSWORD,
            updatedAt = expectedUpdatedAt
        )

        whenever(userPersistencePort.saveOrUpdateUserAccount(any()))
            .thenReturn(savedUser)

        // when
        val result = userManagementService.updateUser(USER_ID, updateModel)

        // then
        assertThat(result).isEqualTo(savedUser)

        val captor = argumentCaptor<UserDomainModel>()
        verify(userPersistencePort).findUserAccountById(USER_ID)
        verify(passwordValidator).validate(PASSWORD)
        verify(passwordEncoderPort).encodePassword(PASSWORD)
        verify(userPersistencePort).saveOrUpdateUserAccount(captor.capture())

        val savedArgument = captor.firstValue
        assertThat(savedArgument.nickname).isNull()
        assertThat(savedArgument.updatedAt).isEqualTo(expectedUpdatedAt)

        verifyNoInteractions(emailVerificationPort)
        verifyNoMoreInteractions(userPersistencePort, passwordValidator, passwordEncoderPort)
    }

    @Test
    fun `should throw NicknameValidationException when updated nickname is empty or whitespaces`() {
        // given
        val existingUser = USER_ACCOUNT_MODEL.copy(
            email = EMAIL,
            nickname = NICKNAME
        )

        whenever(userPersistencePort.findUserAccountById(USER_ID))
            .thenReturn(existingUser)

        whenever(passwordValidator.validate(PASSWORD))
            .thenReturn(emptyList())

        // when & then
        assertThrows<NicknameValidationException> {
            userManagementService.updateUser(
                USER_ID,
                UserHttpDomainModel(
                    email = EMAIL,
                    nickname = "",
                    password = PASSWORD
                )
            )
        }

        assertThrows<NicknameValidationException> {
            userManagementService.updateUser(
                USER_ID,
                UserHttpDomainModel(
                    email = EMAIL,
                    nickname = "   ",
                    password = PASSWORD
                )
            )
        }
    }

    companion object {
        private const val EMAIL = "test@example.com"
        private const val NOT_EXISTING_EMAIL = "test@not-existing.com"
        private const val NICKNAME = "tester"
        private const val PASSWORD = "StrongPassword123!"
        private const val ENCODED_PASSWORD = "encoded-password"
        private val USER_ID = UUID.fromString("700a7ec7-46d8-4545-9eea-f0b3df12691c")
        private val NOT_EXISTING_USER_ID = UUID.fromString("0ab7b8c7-6109-429a-8793-40a632ae071e")
        private const val NEW_EMAIL = "new@example.com"
        private const val NEW_NICKNAME = "new-nickname"

        private val REGISTRATION_MODEL = UserHttpDomainModel(
            email = EMAIL,
            nickname = NICKNAME,
            password = PASSWORD
        )

        private val USER_ACCOUNT_MODEL = UserDomainModel(
            id = USER_ID,
            email = REGISTRATION_MODEL.email,
            nickname = "existing",
            passwordHash = "hash",
            role = UserRole.ADMIN,
            isEmailVerified = true,
            createdAt = OffsetDateTime.now(),
            updatedAt = null,
            lastLoginAt = null,
            accountStatus = AccountStatus.ACTIVE
        )
    }
}
