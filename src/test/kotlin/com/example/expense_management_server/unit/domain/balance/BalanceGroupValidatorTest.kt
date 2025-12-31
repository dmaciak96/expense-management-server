package com.example.expense_management_server.unit.domain.balance

import com.example.expense_management_server.domain.balance.BalanceGroupValidator
import com.example.expense_management_server.domain.balance.exception.BalanceGroupNotFoundException
import com.example.expense_management_server.domain.balance.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balance.model.BalanceGroup
import com.example.expense_management_server.domain.balance.port.BalanceGroupPersistencePort
import com.example.expense_management_server.domain.expense.port.ExpensePersistencePort
import com.example.expense_management_server.domain.user.port.UserPersistencePort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class BalanceGroupValidatorTest {

    @Mock
    private lateinit var balanceGroupPersistencePort: BalanceGroupPersistencePort

    @Mock
    private lateinit var expensePersistencePort: ExpensePersistencePort

    @Mock
    private lateinit var userPersistencePort: UserPersistencePort

    private lateinit var balanceGroupValidator: BalanceGroupValidator

    @BeforeEach
    fun setUp() {
        this.balanceGroupValidator = BalanceGroupValidator(
            balanceGroupPersistencePort = balanceGroupPersistencePort,
            expensePersistencePort = expensePersistencePort,
            userPersistencePort = userPersistencePort
        )
    }

    @Test
    fun `should throw exception when owner not exists`() {
        // given
        whenever(userPersistencePort.findUserAccountById(GROUP_OWNER_ID))
            .thenReturn(null)

        // when & then
        val exception = assertThrows<BalanceGroupValidationException> {
            balanceGroupValidator.validate(BALANCE_GROUP)
        }
        assertThat(exception.message).isEqualTo("Balance group owner does not exist")
    }

    @Test
    fun `should throw exception when name is blank`() {
        // given
        whenever(userPersistencePort.findUserAccountById(GROUP_OWNER_ID))
            .thenReturn(mock())

        // when & then
        val exception = assertThrows<BalanceGroupValidationException> {
            balanceGroupValidator.validate(BALANCE_GROUP.copy(groupName = "   "))
        }
        assertThat(exception.message).isEqualTo("Balance group name cannot contains whitespaces only")
    }

    @Test
    fun `should throw exception when some group member not exists`() {
        // given
        whenever(userPersistencePort.findUserAccountById(GROUP_OWNER_ID))
            .thenReturn(mock())

        // when & then
        val exception = assertThrows<BalanceGroupValidationException> {
            balanceGroupValidator.validate(BALANCE_GROUP)
        }
        assertThat(exception.message).isEqualTo("Some of the members have not been found")
    }

    @Test
    fun `should throw exception when some expense not exists`() {
        // given
        whenever(userPersistencePort.findUserAccountById(GROUP_OWNER_ID))
            .thenReturn(mock())
        whenever(userPersistencePort.findUserAccountById(MEMBER_ID))
            .thenReturn(mock())

        // when & then
        val exception = assertThrows<BalanceGroupValidationException> {
            balanceGroupValidator.validate(BALANCE_GROUP)
        }
        assertThat(exception.message).isEqualTo("Some of the expenses have not been found")
    }

    @Test
    fun `should not throw exception when balance group is valid`() {
        // given
        whenever(userPersistencePort.findUserAccountById(GROUP_OWNER_ID))
            .thenReturn(mock())
        whenever(userPersistencePort.findUserAccountById(MEMBER_ID))
            .thenReturn(mock())
        whenever(expensePersistencePort.getById(EXPENSE_ID))
            .thenReturn(mock())


        // when & then
        assertDoesNotThrow { balanceGroupValidator.validate(BALANCE_GROUP) }
    }

    @Test
    fun `(validate for update) should throw exception when balance group not exists`() {
        // given
        whenever(userPersistencePort.findUserAccountById(GROUP_OWNER_ID))
            .thenReturn(mock())
        whenever(userPersistencePort.findUserAccountById(MEMBER_ID))
            .thenReturn(mock())
        whenever(expensePersistencePort.getById(EXPENSE_ID))
            .thenReturn(mock())

        // when & then
        val exception = assertThrows<BalanceGroupNotFoundException> {
            balanceGroupValidator.validateForUpdate(BALANCE_GROUP_ID, BALANCE_GROUP)
        }
        assertThat(exception.message).isEqualTo("Balance group $BALANCE_GROUP_ID not found")
    }

    @Test
    fun `(validate for update) should not throw exception when balance group is valid`() {
        // given
        whenever(balanceGroupPersistencePort.getById(BALANCE_GROUP_ID))
            .thenReturn(BALANCE_GROUP.copy(groupName = "updated"))
        whenever(userPersistencePort.findUserAccountById(GROUP_OWNER_ID))
            .thenReturn(mock())
        whenever(userPersistencePort.findUserAccountById(MEMBER_ID))
            .thenReturn(mock())
        whenever(expensePersistencePort.getById(EXPENSE_ID))
            .thenReturn(mock())


        // when & then
        assertDoesNotThrow { balanceGroupValidator.validateForUpdate(BALANCE_GROUP_ID, BALANCE_GROUP) }
    }

    companion object {
        private val BALANCE_GROUP_ID = UUID.fromString("72f979a9-a930-4bcb-8996-313ad44a7772")
        private val GROUP_OWNER_ID = UUID.fromString("a40f053f-840d-483e-b329-8287dd89a660")
        private val EXPENSE_ID = UUID.fromString("7f8e2631-d780-405c-85dd-2638f95e3134")
        private val MEMBER_ID = UUID.fromString("51372e8b-83b0-4525-9fe0-c48fea02f39d")
        private const val GROUP_NAME = "test"
        private val BALANCE_GROUP = BalanceGroup(
            id = BALANCE_GROUP_ID,
            groupName = GROUP_NAME,
            groupMemberIds = listOf(GROUP_OWNER_ID, MEMBER_ID),
            expenseIds = listOf(EXPENSE_ID),
            groupOwnerUserId = GROUP_OWNER_ID,
            createdAt = OffsetDateTime.now(),
            updatedAt = OffsetDateTime.now(),
        )
    }
}