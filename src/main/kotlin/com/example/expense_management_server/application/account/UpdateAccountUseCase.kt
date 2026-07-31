package com.example.expense_management_server.application.account

import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.exception.AccountValidationException
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class UpdateAccountUseCase(
    private val accountPersistencePort: AccountPersistencePort,
) {

    fun execute(account: Account): Account {
        LOGGER.info { "Updating account ${account.id} by user ${account.createdBy.email}" }
        val accountToUpdate = accountPersistencePort.findById(account.id)
        if (isNotOwnerButIsMember(accountToUpdate, account.createdBy)) {
            LOGGER.warn { "Account cannot be updated by it's members" }
            throw AccountValidationException("Account cannot be updated by it's members")
        }

        if (isNotOwner(accountToUpdate, account.createdBy)) {
            throw AccountNotFoundException("Account not found by ID ${account.id}")
        }

        val updatedAccount = accountPersistencePort.update(
            accountToUpdate.copy(
                name = account.name,
                currency = account.currency,
                status = account.status,
            )
        )
        LOGGER.info { "Account ${account.id} updated successfully by user ${account.createdBy.email}" }
        return updatedAccount
    }

    private fun isNotOwner(account: Account, currentUser: ApplicationUser) = account.createdBy != currentUser

    private fun isNotOwnerButIsMember(account: Account, currentUser: ApplicationUser) =
        account.createdBy != currentUser && account.members.map { it.applicationUserId }.contains(currentUser.id)

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}