package com.example.expense_management_server.integration

import com.example.expense_management_server.adapter.persistence.model.UserEntity
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.domain.user.port.UserAuthenticationPort
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.client.RestTestClient
import org.testcontainers.containers.PostgreSQLContainer
import java.time.OffsetDateTime

@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTest {

    @Autowired
    protected lateinit var restTestClient: RestTestClient

    @Autowired
    protected lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var userAuthenticationPort: UserAuthenticationPort

    protected lateinit var standardUserOneToken: String
    protected lateinit var standardUserTwoToken: String
    protected lateinit var adminToken: String

    @BeforeEach
    fun createUsers() {
        userRepository.save(
            UserEntity(
                id = null,
                email = STANDARD_USER_EMAIL,
                nickname = null,
                passwordHash = passwordEncoder.encode(STANDARD_PASSWORD)!!,
                isEmailVerified = false,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                lastLoginAt = null,
                role = UserRole.USER,
                accountStatus = AccountStatus.ACTIVE,
            )
        )

        userRepository.save(
            UserEntity(
                id = null,
                email = ADMIN_USER_EMAIL,
                nickname = null,
                passwordHash = passwordEncoder.encode(ADMIN_PASSWORD)!!,
                isEmailVerified = false,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                lastLoginAt = null,
                role = UserRole.ADMIN,
                accountStatus = AccountStatus.ACTIVE,
            )
        )

        userRepository.save(
            UserEntity(
                id = null,
                email = ADDITIONAL_USER_EMAIL,
                nickname = null,
                passwordHash = passwordEncoder.encode(ADDITIONAL_USER_PASSWORD)!!,
                isEmailVerified = false,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                lastLoginAt = null,
                role = UserRole.USER,
                accountStatus = AccountStatus.ACTIVE,
            )
        )

        standardUserOneToken =
            userAuthenticationPort.authenticateAndGenerateJwtToken(STANDARD_USER_EMAIL, STANDARD_PASSWORD)
        standardUserTwoToken =
            userAuthenticationPort.authenticateAndGenerateJwtToken(ADDITIONAL_USER_EMAIL, ADDITIONAL_USER_PASSWORD)
        adminToken = userAuthenticationPort.authenticateAndGenerateJwtToken(ADMIN_USER_EMAIL, ADMIN_PASSWORD)
    }

    @AfterEach
    fun deleteUsers() {
        userRepository.deleteAll()
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun configureDatabaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { PostgresTestContainer.jdbcUrl }
            registry.add("spring.datasource.username") { PostgresTestContainer.username }
            registry.add("spring.datasource.password") { PostgresTestContainer.password }
        }

        protected const val STANDARD_USER_EMAIL = "standard-user@test.com"
        protected const val STANDARD_PASSWORD = "standard-user@tesTcom123"
        protected const val ADMIN_USER_EMAIL = "admin-user@test.com"
        protected const val ADDITIONAL_USER_EMAIL = "additional-user@email.com"
        protected const val ADDITIONAL_USER_PASSWORD = "test-password"
        protected const val ADMIN_PASSWORD = "admin-user@tesTcom123"
    }
}

object PostgresTestContainer : PostgreSQLContainer<PostgresTestContainer>("postgres:16-alpine") {
    init {
        withDatabaseName("test")
        withUsername("test")
        withPassword("test")
        start()
    }
}
