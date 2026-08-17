package com.example.expense_management_server.adapter.api.balance

import com.example.expense_management_server.adapter.api.balance.model.BalanceResponse
import com.example.expense_management_server.application.balance.CalculateBalanceUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/balance")
class BalanceController(
    private val calculateBalanceUseCase: CalculateBalanceUseCase
) {

    @GetMapping
    fun getBalance(@RequestParam accountId: UUID): BalanceResponse {
        val balance = calculateBalanceUseCase.execute(accountId)
        return BalanceResponse(balance)
    }
}