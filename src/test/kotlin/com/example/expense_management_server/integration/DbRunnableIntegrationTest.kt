package com.example.expense_management_server.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class DbRunnableIntegrationTest {

    @LocalServerPort
    var port: Int? = null

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
    }
}