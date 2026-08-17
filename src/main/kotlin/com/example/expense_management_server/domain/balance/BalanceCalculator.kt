package com.example.expense_management_server.domain.balance

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.Expense
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class BalanceCalculator {

    fun calculateBalance(account: Account, applicationUserId: UUID): Long {
        LOGGER.info { "Calculate balance for user: $applicationUserId in account: ${account.id}" }
        if (account.expenses.isEmpty()) {
            LOGGER.info { "No expenses inside of account: ${account.id}, balance is 0" }
            return 0L
        }
        val expensesMap = calculateExpensesMap(account.expenses)
        LOGGER.debug { "Account: ${account.id}, UserID: $applicationUserId, expensesMap: $expensesMap" }

        val accountMemberIds =
            account.members.map { it.applicationUserId.toString() }.toSet() + account.createdBy.id.toString()
        LOGGER.debug { "Account: ${account.id}, UserID: $applicationUserId, memberIds: $accountMemberIds" }

        val shareMap = calculateShareMap(
            totalExpense = expensesMap.getOrDefault(TOTAL_BALANCE_KEY, 0L),
            members = accountMemberIds
        )
        LOGGER.debug { "Account: ${account.id}, UserID: $applicationUserId, shareMap: $shareMap" }

        val userExpenses = expensesMap.getOrDefault(
            key = applicationUserId.toString(),
            defaultValue = 0L
        )
        val userShare = shareMap.getOrDefault(key = applicationUserId.toString(), defaultValue = 0L)
        val balance = userExpenses - userShare
        LOGGER.info { "Account balance for user: $applicationUserId in account: ${account.id} is $balance" }
        return balance
    }

    /**
     * example:
     *  "total" -> 12_000_000
     *  "user_id_2" -> 6_000_000
     *  "user_id_3" -> 6_000_000
     * user_id_1 is missing because he/she didn't make any expenses
     */
    private fun calculateExpensesMap(expenses: Set<Expense>): Map<String, Long> {
        val expensesMap = mutableMapOf(TOTAL_BALANCE_KEY to 0L)
        expenses.forEach {
            expensesMap[TOTAL_BALANCE_KEY] = expensesMap.getOrDefault(TOTAL_BALANCE_KEY, 0L) + it.monetaryAmount
            expensesMap[it.paidBy.id.toString()] =
                expensesMap.getOrDefault(key = it.paidBy.id.toString(), defaultValue = 0L) + it.monetaryAmount
        }
        return expensesMap
    }


    /**
     * example:
     *  total -> 10_000
     *  baseShare per member -> 3333
     *  shareRest -> 1 (will be added to first element of the members Set)
     */
    private fun calculateShareMap(totalExpense: Long, members: Set<String>): Map<String, Long> {
        val baseShare = totalExpense / members.size
        val shareRest = totalExpense % members.size
        val shareMap = mutableMapOf<String, Long>()
        for (member in members) {
            shareMap[member] = baseShare
        }
        shareMap[members.first()] = shareMap.getOrDefault(key = members.first(), defaultValue = 0L) + shareRest
        return shareMap
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
        private const val TOTAL_BALANCE_KEY = "total"
    }
}