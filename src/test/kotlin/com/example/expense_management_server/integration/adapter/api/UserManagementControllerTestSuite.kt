package com.example.expense_management_server.integration.adapter.api

import com.example.expense_management_server.adapter.api.model.UserRequest
import com.example.expense_management_server.adapter.api.model.UserResponse
import com.example.expense_management_server.adapter.persistence.model.UserEntity
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.integration.IntegrationTestSuite
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.ProblemDetail
import org.springframework.test.web.servlet.client.expectBody
import java.time.OffsetDateTime
import kotlin.test.Test

class UserManagementControllerTestSuite : IntegrationTestSuite() {

    @Test
    fun `when provided proper registration data then new user should be registered`() {
        restTestClient.post()
            .uri("/users")
            .body(
                UserRequest(
                    email = VALID_EMAIL,
                    password = VALID_PASSWORD,
                    nickname = VALID_NICKNAME,
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody<UserResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isNotNull()
                assertThat(responseBody.email).isEqualTo(VALID_EMAIL)
                assertThat(responseBody.nickname).isEqualTo(VALID_NICKNAME)
                assertThat(responseBody.emailVerified).isFalse()
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
                assertThat(responseBody.lastLoginAt).isNull()
                assertThat(responseBody.accountStatus).isEqualTo(AccountStatus.ACTIVE)
            }
    }

    @Test
    fun `when e-mail address already exists then should return 400 with proper error details`() {
        restTestClient.post()
            .uri("/users")
            .body(
                UserRequest(
                    email = STANDARD_USER_EMAIL,
                    password = VALID_PASSWORD,
                    nickname = VALID_NICKNAME,
                )
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ProblemDetail>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.status).isEqualTo(400)
                assertThat(responseBody.detail).isEqualTo("User with specified e-mail address $STANDARD_USER_EMAIL already exists")
            }
    }

    @Test
    fun `when password not meet requirements then should return 400 with proper error details`() {
        restTestClient.post()
            .uri("/users")
            .body(
                UserRequest(
                    email = VALID_EMAIL,
                    password = "simplePassword",
                    nickname = VALID_NICKNAME,
                )
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ProblemDetail>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.detail).contains("Password not meet requirements")
            }
    }

    @Test
    fun `when standard user try to fetch account data of another user then should return 403 status code`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.get()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when standard user try to fetch his account data then should return 200 with proper response body`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.get()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(user.email, VALID_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(user.id)
                assertThat(responseBody.email).isEqualTo(user.email)
                assertThat(responseBody.nickname).isEqualTo(user.nickname)
                assertThat(responseBody.emailVerified).isEqualTo(user.isEmailVerified)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isEqualTo(user.updatedAt)
                assertThat(responseBody.lastLoginAt).isEqualTo(user.lastLoginAt)
                assertThat(responseBody.accountStatus).isEqualTo(user.accountStatus)
            }
    }

    @Test
    fun `when admin user try to fetch account data of another user then should return 200 with proper response body`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.get()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(user.id)
                assertThat(responseBody.email).isEqualTo(user.email)
                assertThat(responseBody.nickname).isEqualTo(user.nickname)
                assertThat(responseBody.emailVerified).isEqualTo(user.isEmailVerified)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isEqualTo(user.updatedAt)
                assertThat(responseBody.lastLoginAt).isEqualTo(user.lastLoginAt)
                assertThat(responseBody.accountStatus).isEqualTo(user.accountStatus)
            }
    }

    @Test
    fun `when try to fetch some account data without authentication then return 401 status code`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.get()
            .uri("/users/${user.id}")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `when user try to update all account data then return 200 with proper response body`() {
        // given
        val user = createStandardUser()
        val updatedEmail = "$VALID_EMAIL.updated"
        val updatedPassword = "$VALID_PASSWORD.updated"
        val updatedNickname = "$VALID_NICKNAME.updated"

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .body(
                UserRequest(
                    email = updatedEmail,
                    password = updatedPassword,
                    nickname = updatedNickname,
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(user.id)
                assertThat(responseBody.email).isEqualTo(updatedEmail)
                assertThat(responseBody.nickname).isEqualTo(updatedNickname)
                assertThat(responseBody.emailVerified).isEqualTo(user.isEmailVerified)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNotNull()
                assertThat(responseBody.lastLoginAt).isEqualTo(user.lastLoginAt)
                assertThat(responseBody.accountStatus).isEqualTo(user.accountStatus)
            }

        restTestClient.get()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .exchange()
            .expectStatus().isUnauthorized

        restTestClient.get()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(updatedEmail, updatedPassword) }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `when user try to update e-mail address and nickname then return 200 with proper response body`() {
        // given
        val user = createStandardUser()
        val updatedEmail = "$VALID_EMAIL.updated"
        val updatedNickname = "$VALID_NICKNAME.updated"

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .body(
                UserRequest(
                    email = updatedEmail,
                    password = VALID_PASSWORD,
                    nickname = updatedNickname,
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(user.id)
                assertThat(responseBody.email).isEqualTo(updatedEmail)
                assertThat(responseBody.nickname).isEqualTo(updatedNickname)
                assertThat(responseBody.emailVerified).isEqualTo(user.isEmailVerified)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNotNull()
                assertThat(responseBody.lastLoginAt).isEqualTo(user.lastLoginAt)
                assertThat(responseBody.accountStatus).isEqualTo(user.accountStatus)
            }
    }

    @Test
    fun `when user try to update e-mail address then return 200 with proper response body`() {
        // given
        val user = createStandardUser()
        val updatedEmail = "$VALID_EMAIL.updated"

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .body(
                UserRequest(
                    email = updatedEmail,
                    password = VALID_PASSWORD,
                    nickname = VALID_NICKNAME,
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(user.id)
                assertThat(responseBody.email).isEqualTo(updatedEmail)
                assertThat(responseBody.nickname).isEqualTo(VALID_NICKNAME)
                assertThat(responseBody.emailVerified).isEqualTo(user.isEmailVerified)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNotNull()
                assertThat(responseBody.lastLoginAt).isEqualTo(user.lastLoginAt)
                assertThat(responseBody.accountStatus).isEqualTo(user.accountStatus)
            }
    }

    @Test
    fun `when user try to update e-mail address which already exists for different account then should return 400`() {
        // given
        val user = createStandardUser()
        val updatedEmail = STANDARD_USER_EMAIL

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .body(
                UserRequest(
                    email = updatedEmail,
                    password = VALID_PASSWORD,
                    nickname = VALID_NICKNAME,
                )
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ProblemDetail>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.status).isEqualTo(400)
                assertThat(responseBody.detail).isEqualTo("User with specified e-mail address $STANDARD_USER_EMAIL already exists")
            }
    }

    @Test
    fun `when user try to update password but not meets requirements then return 400 with proper error details`() {
        // given
        val user = createStandardUser()
        val updatedPassword = "simple password"

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .body(
                UserRequest(
                    email = VALID_EMAIL,
                    password = updatedPassword,
                    nickname = VALID_NICKNAME,
                )
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ProblemDetail>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.detail).contains("Password not meet requirements")
            }
    }

    @Test
    fun `when user try to update nickname but he sends empty string then should return 400`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .body(
                UserRequest(
                    email = VALID_EMAIL,
                    password = VALID_PASSWORD,
                    nickname = "",
                )
            )
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ProblemDetail>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.status).isEqualTo(400)
                assertThat(responseBody.detail).isEqualTo("Nickname is empty after removing whitespaces")
            }
    }

    @Test
    fun `when standard user try to update not his account then return 403 status code`() {
        // given
        val user = createStandardUser()
        val updatedEmail = "$VALID_EMAIL.updated"
        val updatedPassword = "$VALID_PASSWORD.updated"
        val updatedNickname = "$VALID_NICKNAME.updated"

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .body(
                UserRequest(
                    email = updatedEmail,
                    password = updatedPassword,
                    nickname = updatedNickname,
                )
            )
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when admin user try to update not his account then should return 200 status code`() {
        // given
        val user = createStandardUser()
        val updatedEmail = "$VALID_EMAIL.updated"
        val updatedPassword = "$VALID_PASSWORD.updated"
        val updatedNickname = "$VALID_NICKNAME.updated"

        // when & then
        restTestClient.put()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .body(
                UserRequest(
                    email = updatedEmail,
                    password = updatedPassword,
                    nickname = updatedNickname,
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody<UserResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(user.id)
                assertThat(responseBody.email).isEqualTo(updatedEmail)
                assertThat(responseBody.nickname).isEqualTo(updatedNickname)
                assertThat(responseBody.emailVerified).isEqualTo(user.isEmailVerified)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNotNull()
                assertThat(responseBody.lastLoginAt).isEqualTo(user.lastLoginAt)
                assertThat(responseBody.accountStatus).isEqualTo(user.accountStatus)
            }
    }

    @Test
    fun `when standard user try to delete his account then return 204 status code`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.delete()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(VALID_EMAIL, VALID_PASSWORD) }
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `when standard user try to delete not his account then return 403 status code`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.delete()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when admin user try to delete not his account then return 204 status code`() {
        // given
        val user = createStandardUser()

        // when & then
        restTestClient.delete()
            .uri("/users/${user.id}")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .exchange()
            .expectStatus().isNoContent
    }

    private fun createStandardUser() = userRepository.save(
        UserEntity(
            id = null,
            email = VALID_EMAIL,
            nickname = VALID_NICKNAME,
            passwordHash = passwordEncoder.encode(VALID_PASSWORD)!!,
            isEmailVerified = false,
            createdAt = OffsetDateTime.now(),
            updatedAt = null,
            lastLoginAt = null,
            role = UserRole.USER,
            accountStatus = AccountStatus.ACTIVE,
        )
    )

    companion object {
        private const val VALID_EMAIL = "test-email-user@test-email.com"
        private const val VALID_PASSWORD = "TestP@ssw0rd"
        private const val VALID_NICKNAME = "testNickname"
    }
}