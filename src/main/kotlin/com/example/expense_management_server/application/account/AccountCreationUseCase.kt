package com.example.expense_management_server.application.account

import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class AccountCreationUseCase(
    private val accountPersistencePort: AccountPersistencePort,
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase
) {
    fun execute(account: Account): Account {
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        LOGGER.info { "Creating new account ${account.name} by ${currentUser.email}" }
        val savedAccount = accountPersistencePort.create(
            account.copy(
                createdBy = currentUser
            )
        )
        LOGGER.info { "Account ${account.name} created successfully" }
        return savedAccount
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}