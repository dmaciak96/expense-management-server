package com.example.expense_management_server.application.balance

import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.balance.BalanceCalculator
import org.springframework.stereotype.Component
import java.util.*

@Component
class CalculateBalanceUseCase(
    private val balanceCalculator: BalanceCalculator,
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val fetchAccountByIdUseCase: FetchAccountByIdUseCase
) {

    fun execute(accountId: UUID): Long {
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        val account = fetchAccountByIdUseCase.execute(accountId)
        return balanceCalculator.calculateBalance(
            account = account,
            applicationUserId = currentUser.id
        )
    }
}