package com.example.expense_management_server

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<ExpenseManagementServerApplication>().with(TestcontainersConfiguration::class).run(*args)
}
