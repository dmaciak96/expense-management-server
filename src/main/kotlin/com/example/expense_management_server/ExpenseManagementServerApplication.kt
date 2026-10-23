package com.example.expense_management_server

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(
    info = Info(
        title = "Expense Management API",
        description = "API documentation for Expense Management APP"
    )
)
@SpringBootApplication
class ExpenseManagementServerApplication

fun main(args: Array<String>) {
    runApplication<ExpenseManagementServerApplication>(*args)
}
