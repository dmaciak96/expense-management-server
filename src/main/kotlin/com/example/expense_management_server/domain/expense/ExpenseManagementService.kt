package com.example.expense_management_server.domain.expense

import com.example.expense_management_server.domain.expense.model.Expense
import com.example.expense_management_server.domain.expense.port.IExpensePersistencePort
import com.example.expense_management_server.domain.facade.IExpenseManagementFacade
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ExpenseManagementService(
    private val expensePersistencePort: IExpensePersistencePort,
    private val expenseValidator: ExpenseValidator,
) : IExpenseManagementFacade {

    override fun save(expense: Expense): Expense {
        LOGGER.info { "Saving expense $expense" }

        LOGGER.info { "Validating expense $expense" }
        expenseValidator.validate(expense)
        LOGGER.info { "Expense $expense is valid" }

        return expensePersistencePort.save(expense)
    }

    override fun update(
        expenseId: UUID,
        expense: Expense
    ): Expense {
        LOGGER.info { "Updating expense $expenseId" }

        LOGGER.info { "Validating expense $expense" }
        expenseValidator.validateForUpdate(expenseId, expense)
        LOGGER.info { "Expense $expense is valid" }

        return expensePersistencePort.update(expenseId, expense)
    }

    override fun delete(expenseId: UUID) {
        LOGGER.info { "Deleting expense $expenseId" }
        expenseValidator.checkIfExpenseExists(expenseId)
        expensePersistencePort.delete(expenseId)
    }

    override fun getById(expenseId: UUID): Expense {
        LOGGER.info { "Fetching expense $expenseId" }
        expenseValidator.checkIfExpenseExists(expenseId)
        return expensePersistencePort.getById(expenseId)!!
    }

    override fun getAllByBalanceGroup(balanceGroupId: UUID): List<Expense> {
        LOGGER.info { "Fetching all expenses for balance group $balanceGroupId" }
        expenseValidator.checkBalanceGroupExists(balanceGroupId)
        return expensePersistencePort.getAllByBalanceGroup(balanceGroupId)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}