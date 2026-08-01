package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.application.application_user.FetchUserByIdUseCase
import com.example.expense_management_server.domain.account.exception.AccountValidationException
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountMember
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class AddMemberToAccountUseCase(
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase,
    private val fetchUserByIdUseCase: FetchUserByIdUseCase,
    private val accountPersistencePort: AccountPersistencePort,
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase
) {

    fun execute(accountId: UUID, applicationUserId: UUID): Account {
        LOGGER.info { "Adding new member $applicationUserId to account $accountId" }
        val account = fetchAccountByIdUseCase.execute(accountId)
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        if (isNotAccountOwner(account, currentUser)) {
            throw AccountValidationException("Members can be added only by account creator")
        }
        if (account.members.any { it.applicationUserId == applicationUserId }) {
            throw AccountValidationException("Member $applicationUserId already exists inside account $accountId")
        }
        val user = fetchUserByIdUseCase.execute(accountId)
        val updatedAccount = accountPersistencePort.addAccountMember(
            accountId = accountId,
            accountMember = AccountMember(
                applicationUserId = applicationUserId,
                firstName = user.firstName,
                lastName = user.lastName,
                displayName = user.displayName,
                avatarUrl = user.avatarUrl,
            )
        )
        LOGGER.info { "New member $applicationUserId was added to account $accountId" }
        return updatedAccount
    }

    private fun isNotAccountOwner(account: Account, user: ApplicationUser) = account.createdBy != user

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}