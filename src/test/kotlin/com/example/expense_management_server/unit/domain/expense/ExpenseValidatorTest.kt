package com.example.expense_management_server.unit.domain.expense

import com.example.expense_management_server.domain.balancegroup.model.BalanceGroupDomainModel
import com.example.expense_management_server.domain.balancegroup.port.IBalanceGroupPersistencePort
import com.example.expense_management_server.domain.expense.ExpenseValidator
import com.example.expense_management_server.domain.expense.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.expense.exception.ExpenseValidationException
import com.example.expense_management_server.domain.expense.model.ExpenseDomainModel
import com.example.expense_management_server.domain.expense.model.ExpenseSplitType
import com.example.expense_management_server.domain.expense.port.IExpensePersistencePort
import com.example.expense_management_server.domain.user.model.AccountStatus
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.model.UserRole
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ExpenseValidatorTest {

    @Mock
    private lateinit var expensePersistencePort: IExpensePersistencePort

    @Mock
    private lateinit var balanceGroupPersistencePort: IBalanceGroupPersistencePort

    @Mock
    private lateinit var userPersistencePort: IUserPersistencePort

    private lateinit var expenseValidator: ExpenseValidator

    @BeforeEach
    fun initialize() {
        expenseValidator = ExpenseValidator(
            expensePersistencePort = expensePersistencePort,
            balanceGroupPersistencePort = balanceGroupPersistencePort,
            userPersistencePort = userPersistencePort
        )
    }

    @Test
    fun `should validate expense when name is not blank, balance group exists, user is member and amount is positive`() {
        // given
        val balanceGroup = mock<BalanceGroupDomainModel>()
        whenever(balanceGroup.groupMemberIds).thenReturn(listOf(EXPENSE_OWNER_ID))
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID)).thenReturn(balanceGroup)
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)

        // when
        expenseValidator.validate(EXPENSE_MODEL)

        // then
        verifyNoMoreInteractions(balanceGroupPersistencePort, expensePersistencePort)
    }

    @Test
    fun `should throw ExpenseValidationException when expense name is blank`() {
        // given
        val expense = EXPENSE_MODEL.copy(name = "   ")
        val balanceGroup = mock<BalanceGroupDomainModel>()
        whenever(balanceGroup.groupMemberIds).thenReturn(listOf(EXPENSE_OWNER_ID))
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID)).thenReturn(balanceGroup)
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)

        // when & then
        val ex = assertThrows<ExpenseValidationException> {
            expenseValidator.validate(expense)
        }
        assertThat(ex.message).isEqualTo("Expense name cannot contains whitespaces only")
    }

    @Test
    fun `should throw ExpenseValidationException when balance group does not exist`() {
        // given
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID)).thenReturn(null)
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)

        // when & then
        val ex = assertThrows<ExpenseValidationException> {
            expenseValidator.validate(EXPENSE_MODEL)
        }
        assertThat(ex.message).isEqualTo("Balance group does not exist")

        verify(balanceGroupPersistencePort).getById(BALANCE_GROUP_ID)
        verifyNoMoreInteractions(balanceGroupPersistencePort, expensePersistencePort)
    }

    @Test
    fun `should throw ExpenseValidationException when current user is not a member of balance group`() {
        // given
        val balanceGroup = mock<BalanceGroupDomainModel>()
        whenever(balanceGroup.groupMemberIds).thenReturn(listOf(OTHER_USER_ID))
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID)).thenReturn(balanceGroup)
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)

        // when & then
        val ex = assertThrows<ExpenseValidationException> {
            expenseValidator.validate(EXPENSE_MODEL)
        }
        assertThat(ex.message)
            .contains("Expense creator is not a member of balance group")
    }

    @Test
    fun `should throw ExpenseValidationException when amount is zero`() {
        // given
        val balanceGroup = mock<BalanceGroupDomainModel>()
        whenever(balanceGroup.groupMemberIds).thenReturn(listOf(EXPENSE_OWNER_ID))
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID)).thenReturn(balanceGroup)
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)

        // when & then
        val ex = assertThrows<ExpenseValidationException> {
            expenseValidator.validate(EXPENSE_MODEL.copy(amount = 0.0))
        }
        assertThat(ex.message).isEqualTo("Amount must be positive value")
        verifyNoMoreInteractions(balanceGroupPersistencePort, expensePersistencePort)
    }

    @Test
    fun `should throw ExpenseValidationException when amount is negative`() {
        // given
        val balanceGroup = mock<BalanceGroupDomainModel>()
        whenever(balanceGroup.groupMemberIds).thenReturn(listOf(EXPENSE_OWNER_ID))
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID)).thenReturn(balanceGroup)
        whenever(userPersistencePort.findUserAccountById(EXPENSE_OWNER_ID)).thenReturn(EXPENSE_CREATOR)

        // when & then
        val ex = assertThrows<ExpenseValidationException> {
            expenseValidator.validate(EXPENSE_MODEL.copy(amount = -10.0))
        }
        assertThat(ex.message).isEqualTo("Amount must be positive value")
        verifyNoMoreInteractions(balanceGroupPersistencePort, expensePersistencePort)
    }

    @Test
    fun `should throw ExpenseNotFoundException when expense does not exist`() {
        // given
        whenever(expensePersistencePort.getById(EXPENSE_ID)).thenReturn(null)

        // when & then
        val ex = assertThrows<ExpenseNotFoundException> {
            expenseValidator.checkIfExpenseExists(EXPENSE_ID)
        }
        assertThat(ex.message).isEqualTo("Expense $EXPENSE_ID not found")

        verify(expensePersistencePort).getById(EXPENSE_ID)
        verifyNoMoreInteractions(expensePersistencePort, balanceGroupPersistencePort)
    }

    @Test
    fun `should not throw when expense exists`() {
        // given
        whenever(expensePersistencePort.getById(EXPENSE_ID)).thenReturn(mock())

        // when
        expenseValidator.checkIfExpenseExists(EXPENSE_ID)

        // then
        verify(expensePersistencePort).getById(EXPENSE_ID)
        verifyNoMoreInteractions(expensePersistencePort, balanceGroupPersistencePort)
    }

    @Test
    fun `should throw ExpenseValidationException when checking balance group and it does not exist`() {
        // given
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID)).thenReturn(null)

        // when & then
        val ex = assertThrows<ExpenseValidationException> {
            expenseValidator.checkBalanceGroupExists(BALANCE_GROUP_ID)
        }
        assertThat(ex.message).isEqualTo("Balance group does not exist")

        verify(balanceGroupPersistencePort).getById(BALANCE_GROUP_ID)
        verifyNoMoreInteractions(expensePersistencePort, balanceGroupPersistencePort)
    }

    companion object {
        private val EXPENSE_ID = UUID.fromString("c4c3e0bf-5b1f-49ff-bb2c-5d6b6db5f16c")
        private val BALANCE_GROUP_ID = UUID.fromString("f2bd2b1c-8fb6-4e65-a52e-4c4b9f9b2a0f")
        private val OTHER_USER_ID = UUID.fromString("0ab7b8c7-6109-429a-8793-40a632ae071e")
        private val EXPENSE_OWNER_ID = UUID.fromString("c4c3e0bf-5b1f-49ff-bb2c-5d6b6db5f16d")
        private const val EXPENSE_NAME = "Lunch"
        private const val AMOUNT = 10.0
        private val EXPENSE_MODEL = ExpenseDomainModel(
            id = EXPENSE_ID,
            name = EXPENSE_NAME,
            balanceGroupId = BALANCE_GROUP_ID,
            amount = AMOUNT,
            splitType = ExpenseSplitType.EQUALLY,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now(),
            expenseOwnerId = EXPENSE_OWNER_ID
        )
        private val EXPENSE_CREATOR = UserDomainModel(
            id = EXPENSE_OWNER_ID, email = "",
            nickname = "",
            passwordHash = "",
            role = UserRole.USER,
            isEmailVerified = false,
            createdAt = OffsetDateTime.now(),
            updatedAt = null,
            lastLoginAt = null,
            accountStatus = AccountStatus.ACTIVE
        )
    }
}
