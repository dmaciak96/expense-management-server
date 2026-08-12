package com.example.expense_management_server.domain.balance

import com.example.expense_management_server.domain.account.model.Account
import org.springframework.stereotype.Component
import java.util.*

@Component
class BalanceCalculator {

    fun calculateBalance(account: Account, applicationUserId: UUID): Long {
        throw NotImplementedError()
    }
}