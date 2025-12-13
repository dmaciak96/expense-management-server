package com.example.expense_management_server.integration

import com.example.expense_management_server.adapter.persistence.UserEntity
import com.example.expense_management_server.adapter.persistence.UserRepository
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserRole
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
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.OffsetDateTime

@Testcontainers
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestSuite {

    @Autowired
    protected lateinit var restTestClient: RestTestClient

    @Autowired
    protected lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    protected lateinit var userRepository: UserRepository

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
    }

    @AfterEach
    fun deleteUsers() {
        userRepository.deleteAll()
    }

    companion object {

        @JvmStatic
        @DynamicPropertySource
        fun configureDatabaseProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.getJdbcUrl() }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }

        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:18.1-alpine")

        protected const val STANDARD_USER_EMAIL = "standard-user@test.com"
        protected const val STANDARD_PASSWORD = "standard-user@tesTcom123"
        protected const val ADMIN_USER_EMAIL = "admin-user@test.com"
        protected const val ADMIN_PASSWORD = "admin-user@tesTcom123"
    }
}