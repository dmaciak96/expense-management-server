package com.example.expense_management_server.application.account

import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class FetchAllAccountsUseCase(
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val accountPersistencePort: AccountPersistencePort
) {
    fun execute(): List<Account> {
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        LOGGER.info { "Searching all accounts created by (or membered) ${currentUser.email}" }
        val createdBy = accountPersistencePort.findAllByCreatorId(currentUser.id)
        val membered = accountPersistencePort.findAllByMemberId(currentUser.id)
        return createdBy + membered
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}