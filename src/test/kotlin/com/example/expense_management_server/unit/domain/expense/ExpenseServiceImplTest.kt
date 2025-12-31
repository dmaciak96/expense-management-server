package com.example.expense_management_server.unit.domain.expense

import com.example.expense_management_server.domain.expense.ExpenseServiceImpl
import com.example.expense_management_server.domain.expense.ExpenseValidator
import com.example.expense_management_server.domain.expense.model.Expense
import com.example.expense_management_server.domain.expense.port.ExpensePersistencePort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ExpenseServiceImplTest {

    @Mock
    private lateinit var expensePersistencePort: ExpensePersistencePort

    @Mock
    private lateinit var expenseValidator: ExpenseValidator

    private lateinit var expenseServiceImpl: ExpenseServiceImpl

    @BeforeEach
    fun initialize() {
        expenseServiceImpl = ExpenseServiceImpl(
            expensePersistencePort = expensePersistencePort,
            expenseValidator = expenseValidator
        )
    }

    @Test
    fun `should save expense when expense is valid`() {
        // given
        whenever(expensePersistencePort.save(EXPENSE_MODEL))
            .thenReturn(EXPENSE_MODEL)

        // when
        val result = expenseServiceImpl.save(EXPENSE_MODEL)

        // then
        assertThat(result).isEqualTo(EXPENSE_MODEL)

        verify(expenseValidator).validate(EXPENSE_MODEL)
        verify(expensePersistencePort).save(EXPENSE_MODEL)

        verifyNoMoreInteractions(expenseValidator, expensePersistencePort)
    }

    @Test
    fun `should update expense when expense exists and expense is valid`() {
        // given
        whenever(expensePersistencePort.update(EXPENSE_ID, EXPENSE_MODEL))
            .thenReturn(EXPENSE_MODEL)

        // when
        val result = expenseServiceImpl.update(EXPENSE_ID, EXPENSE_MODEL)

        // then
        assertThat(result).isEqualTo(EXPENSE_MODEL)

        verify(expenseValidator).validateForUpdate(EXPENSE_ID, EXPENSE_MODEL)
        verify(expensePersistencePort).update(EXPENSE_ID, EXPENSE_MODEL)

        verifyNoMoreInteractions(expenseValidator, expensePersistencePort)
    }

    @Test
    fun `should delete expense when expense exists`() {
        // when
        expenseServiceImpl.delete(EXPENSE_ID)

        // then
        verify(expenseValidator).checkIfExpenseExists(EXPENSE_ID)
        verify(expensePersistencePort).delete(EXPENSE_ID)

        verifyNoMoreInteractions(expenseValidator, expensePersistencePort)
    }

    @Test
    fun `should get expense by id when expense exists`() {
        // given
        whenever(expensePersistencePort.getById(EXPENSE_ID))
            .thenReturn(EXPENSE_MODEL)

        // when
        val result = expenseServiceImpl.getById(EXPENSE_ID)

        // then
        assertThat(result).isEqualTo(EXPENSE_MODEL)

        verify(expenseValidator).checkIfExpenseExists(EXPENSE_ID)
        verify(expensePersistencePort).getById(EXPENSE_ID)

        verifyNoMoreInteractions(expenseValidator, expensePersistencePort)
    }

    @Test
    fun `should get all expenses by balance group when balance group is valid`() {
        // given
        whenever(expensePersistencePort.getAllByBalanceGroup(BALANCE_GROUP_ID))
            .thenReturn(EXPENSES)

        // when
        val result = expenseServiceImpl.getAllByBalanceGroup(BALANCE_GROUP_ID)

        // then
        assertThat(result).isEqualTo(EXPENSES)

        verify(expenseValidator).checkBalanceGroupExists(BALANCE_GROUP_ID)
        verify(expensePersistencePort).getAllByBalanceGroup(BALANCE_GROUP_ID)

        verifyNoMoreInteractions(expenseValidator, expensePersistencePort)
    }

    @Test
    fun `should not call persistence when save validation fails`() {
        // given
        whenever(expenseValidator.validate(EXPENSE_MODEL))
            .thenThrow(RuntimeException("validation failed"))

        // when & then
        assertThrows<RuntimeException> {
            expenseServiceImpl.save(EXPENSE_MODEL)
        }

        verify(expenseValidator).validate(EXPENSE_MODEL)
        verifyNoInteractions(expensePersistencePort)
        verifyNoMoreInteractions(expenseValidator)
    }

    @Test
    fun `should not call persistence when update existence check fails`() {
        // given
        whenever(expenseValidator.validateForUpdate(EXPENSE_ID, EXPENSE_MODEL))
            .thenThrow(RuntimeException("not found"))

        // when & then
        assertThrows<RuntimeException> {
            expenseServiceImpl.update(EXPENSE_ID, EXPENSE_MODEL)
        }

        verifyNoInteractions(expensePersistencePort)
        verifyNoMoreInteractions(expenseValidator)
    }

    @Test
    fun `should not call persistence when delete existence check fails`() {
        // given
        whenever(expenseValidator.checkIfExpenseExists(EXPENSE_ID))
            .thenThrow(RuntimeException("not found"))

        // when & then
        assertThrows<RuntimeException> {
            expenseServiceImpl.delete(EXPENSE_ID)
        }

        verify(expenseValidator).checkIfExpenseExists(EXPENSE_ID)
        verifyNoInteractions(expensePersistencePort)
        verifyNoMoreInteractions(expenseValidator)
    }

    @Test
    fun `should not call persistence when balance group validation fails`() {
        // given
        whenever(expenseValidator.checkBalanceGroupExists(BALANCE_GROUP_ID))
            .thenThrow(RuntimeException("invalid balance group"))

        // when & then
        assertThrows<RuntimeException> {
            expenseServiceImpl.getAllByBalanceGroup(BALANCE_GROUP_ID)
        }

        verify(expenseValidator).checkBalanceGroupExists(BALANCE_GROUP_ID)
        verifyNoInteractions(expensePersistencePort)
        verifyNoMoreInteractions(expenseValidator)
    }

    companion object {
        private val EXPENSE_ID = UUID.fromString("9e57d9dc-1c37-49f0-8c05-7e0ff7b3b5a8")
        private val BALANCE_GROUP_ID = UUID.fromString("f2bd2b1c-8fb6-4e65-a52e-4c4b9f9b2a0f")

        private val EXPENSE_MODEL: Expense = mock()
        private val EXPENSES = listOf(EXPENSE_MODEL)
    }
}