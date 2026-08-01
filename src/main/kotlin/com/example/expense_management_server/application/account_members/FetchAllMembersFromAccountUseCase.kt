package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.model.AccountMember
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class FetchAllMembersFromAccountUseCase(
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase
) {

    fun execute(accountId: UUID): List<AccountMember> {
        LOGGER.info { "Fetching all members from account $accountId" }
        val account = fetchAccountByIdUseCase.execute(accountId)
        val members = account.members
        LOGGER.info { "Found ${members.size} members in account $accountId" }
        return members.toList()
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}