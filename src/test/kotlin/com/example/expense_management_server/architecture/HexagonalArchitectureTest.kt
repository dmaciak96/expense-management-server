package com.example.expense_management_server.architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.onionArchitecture
import org.junit.jupiter.api.Test

class HexagonalArchitectureTest {

    @Test
    fun `hexagonal software architecture check`() {
        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(BASE_PACKAGE)

        onionArchitecture()
            .domainModels(USER_DOMAIN_MODEL)
            .domainServices(USER_DOMAIN_SERVICE)
            .applicationServices(APPLICATION_SERVICES)
            .adapter("persistence", PERSISTENCE_ADAPTER)
            .adapter("api", API_ADAPTER)
            .adapter("security", SECURITY_ADAPTER)
            .adapter("notification", NOTIFICATION_ADAPTER)

            .check(importedClasses)
    }

    companion object {
        private const val BASE_PACKAGE = "com.example.expense_management_server"
        private const val USER_DOMAIN_MODEL = "$BASE_PACKAGE.domain.user.."
        private const val APPLICATION_SERVICES = "$BASE_PACKAGE.domain.."
        private const val USER_DOMAIN_SERVICE = "$BASE_PACKAGE.domain.user.."
        private const val PERSISTENCE_ADAPTER = "$BASE_PACKAGE.adapter.persistence.."
        private const val API_ADAPTER = "$BASE_PACKAGE.adapter.api.."
        private const val SECURITY_ADAPTER = "$BASE_PACKAGE.adapter.security.."
        private const val NOTIFICATION_ADAPTER = "$BASE_PACKAGE.adapter.notification.."

    }
}