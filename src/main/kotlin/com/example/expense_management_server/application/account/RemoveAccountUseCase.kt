package com.example.expense_management_server.application.account

import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class RemoveAccountUseCase(
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val accountPersistencePort: AccountPersistencePort
) {

    fun execute(accountId: UUID) {
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        LOGGER.info { "Removing account by ID by user ${currentUser.email}" }
        val account = accountPersistencePort.findById(accountId)
        if (account.createdBy.id != currentUser.id) {
            throw AccountNotFoundException("Account with ID $accountId not found")
        }
        accountPersistencePort.deleteById(accountId)
        LOGGER.info { "Account $accountId was deleted successfully" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}