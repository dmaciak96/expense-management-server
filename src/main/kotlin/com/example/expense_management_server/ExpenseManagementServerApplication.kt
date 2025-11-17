package com.example.expense_management_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExpenseManagementServerApplication

fun main(args: Array<String>) {
	runApplication<ExpenseManagementServerApplication>(*args)
}
