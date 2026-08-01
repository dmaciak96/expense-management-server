package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.exception.AccountMemberNotFoundException
import com.example.expense_management_server.domain.account.model.AccountMember
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class FetchMemberByIdAndAccountIdUseCase(
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase
) {

    fun execute(accountId: UUID, applicationUserId: UUID): AccountMember {
        LOGGER.info { "Fetching member $applicationUserId from account $accountId" }
        val account = fetchAccountByIdUseCase.execute(accountId)
        try {
            val member = account.members.first { it.applicationUserId == applicationUserId }
            LOGGER.info { "Member was found" }
            return member
        } catch (e: NoSuchElementException) {
            throw AccountMemberNotFoundException("Member $applicationUserId not found inside account $accountId")
        }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}