package com.example.expense_management_server.domain.expense.exception

import java.util.UUID

class ExpenseNotFoundException(expenseId: UUID) : RuntimeException("Expense $expenseId not found")