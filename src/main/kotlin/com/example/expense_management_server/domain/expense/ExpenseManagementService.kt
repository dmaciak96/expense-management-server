package com.example.expense_management_server.domain.expense

import com.example.expense_management_server.domain.expense.model.ExpenseDomainModel
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

    override fun save(expenseDomainModel: ExpenseDomainModel): ExpenseDomainModel {
        LOGGER.info { "Saving expense $expenseDomainModel" }

        LOGGER.info { "Validating expense $expenseDomainModel" }
        expenseValidator.validate(expenseDomainModel)
        LOGGER.info { "Expense $expenseDomainModel is valid" }

        return expensePersistencePort.save(expenseDomainModel)
    }

    override fun update(
        expenseId: UUID,
        expenseDomainModel: ExpenseDomainModel
    ): ExpenseDomainModel {
        LOGGER.info { "Updating expense $expenseId" }

        LOGGER.info { "Validating expense $expenseDomainModel" }
        expenseValidator.validateForUpdate(expenseId, expenseDomainModel)
        LOGGER.info { "Expense $expenseDomainModel is valid" }

        return expensePersistencePort.update(expenseId, expenseDomainModel)
    }

    override fun delete(expenseId: UUID) {
        LOGGER.info { "Deleting expense $expenseId" }
        expenseValidator.checkIfExpenseExists(expenseId)
        expensePersistencePort.delete(expenseId)
    }

    override fun getById(expenseId: UUID): ExpenseDomainModel {
        LOGGER.info { "Fetching expense $expenseId" }
        expenseValidator.checkIfExpenseExists(expenseId)
        return expensePersistencePort.getById(expenseId)!!
    }

    override fun getAllByBalanceGroup(balanceGroupId: UUID): List<ExpenseDomainModel> {
        LOGGER.info { "Fetching all expenses for balance group $balanceGroupId" }
        expenseValidator.checkBalanceGroupExists(balanceGroupId)
        return expensePersistencePort.getAllByBalanceGroup(balanceGroupId)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}