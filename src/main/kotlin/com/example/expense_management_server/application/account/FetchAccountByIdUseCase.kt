package com.example.expense_management_server.application.account

import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class FetchAccountByIdUseCase(
    private val accountPersistencePort: AccountPersistencePort,
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase
) {

    fun execute(id: UUID): Account {
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        LOGGER.info { "Fetching account by ID by user ${currentUser.email}" }
        val account = accountPersistencePort.findById(id)
        if (isNotAccountOwner(account, currentUser) || isNotAccountMember(account, currentUser)) {
            throw AccountNotFoundException("Account with ID $id not found")
        }
        return account
    }

    private fun isNotAccountOwner(account: Account, user: ApplicationUser) = account.createdBy != user
    private fun isNotAccountMember(account: Account, user: ApplicationUser) =
        !account.members.map { it.applicationUserId }
            .contains(user.id)

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}