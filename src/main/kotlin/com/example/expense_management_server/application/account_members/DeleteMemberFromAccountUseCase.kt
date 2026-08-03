package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountValidationException
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class DeleteMemberFromAccountUseCase(
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase,
    private val accountPersistencePort: AccountPersistencePort
) {

    fun execute(accountId: UUID, applicationUserId: UUID): Account {
        LOGGER.info { "Removing member $applicationUserId from account $accountId" }
        val account = fetchAccountByIdUseCase.execute(accountId)
        if (isNotAccountOwner(account, fetchCurrentLoginUserUseCase.execute())) {
            throw AccountValidationException("Account members can be removed only by account creator")
        }
        val updatedAccount = accountPersistencePort.removeMember(accountId, applicationUserId)
        LOGGER.info { "Member $applicationUserId has been deleted successfully from account $accountId" }
        return updatedAccount
    }

    private fun isNotAccountOwner(account: Account, user: ApplicationUser) = account.createdBy.id != user.id

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}