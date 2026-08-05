package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.persistence.model.AccountEntity
import com.example.expense_management_server.adapter.persistence.model.AccountMemberEntity
import com.example.expense_management_server.adapter.persistence.model.ExpenseEntity
import com.example.expense_management_server.adapter.persistence.repository.AccountRepository
import com.example.expense_management_server.domain.account.exception.AccountMemberNotFoundException
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.exception.ExpenseNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.Optional
import java.util.UUID

class AccountPersistenceAdapterTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var adapter: AccountPersistenceAdapter

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        adapter = AccountPersistenceAdapter(
            accountRepository = accountRepository
        )
    }

    @Test
    fun `should create account`() {
        // given
        val entity = AccountEntity.fromDomain(TestConstants.ACCOUNT)

        whenever(accountRepository.save(entity))
            .thenReturn(entity)

        // when
        val result = adapter.create(TestConstants.ACCOUNT)

        // then
        assertEquals(TestConstants.ACCOUNT, result)

        verify(accountRepository).save(entity)
    }

    @Test
    fun `should find account by id`() {
        // given
        val entity = AccountEntity.fromDomain(TestConstants.ACCOUNT)

        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(entity))

        // when
        val result = adapter.findById(TestConstants.ACCOUNT_ID)

        // then
        assertEquals(TestConstants.ACCOUNT, result)

        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
    }

    @Test
    fun `should throw AccountNotFoundException when account does not exist`() {
        // given
        val id = UUID.randomUUID()

        whenever(accountRepository.findById(id.toString()))
            .thenReturn(Optional.empty())

        // when & then
        assertThrows<AccountNotFoundException> {
            adapter.findById(id)
        }

        verify(accountRepository).findById(id.toString())
    }

    @Test
    fun `should add expense`() {
        // given
        val accountWithoutExpenses = TestConstants.ACCOUNT.copy(expenses = emptySet())
        val entity = AccountEntity.fromDomain(accountWithoutExpenses)
        val updatedEntity = entity.copy(
            expenses = entity.expenses + ExpenseEntity.fromDomain(TestConstants.EXPENSE)
        )

        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(entity))

        whenever(accountRepository.save(updatedEntity))
            .thenReturn(updatedEntity)

        // when
        val result = adapter.addExpense(
            TestConstants.EXPENSE,
            TestConstants.ACCOUNT_ID
        )

        // then
        assertEquals(setOf(TestConstants.EXPENSE), result.expenses)

        verify(accountRepository).save(updatedEntity)
    }

    @Test
    fun `should throw ExpenseNotFoundException when removing unknown expense`() {
        // given
        val entity = AccountEntity.fromDomain(TestConstants.ACCOUNT)
        val expenseId = UUID.randomUUID()

        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(entity))

        // when & then
        assertThrows<ExpenseNotFoundException> {
            adapter.removeExpense(
                TestConstants.ACCOUNT_ID,
                expenseId
            )
        }

        verify(accountRepository, never()).save(org.mockito.kotlin.any())
    }

    @Test
    fun `should add account member`() {
        // given
        val account = TestConstants.ACCOUNT.copy(
            members = setOf(TestConstants.APPLICATION_USER_ONE.toAccountMember())
        )
        val entity = AccountEntity.fromDomain(account)
        val member = TestConstants.APPLICATION_USER_TWO.toAccountMember()
        val updatedEntity = entity.copy(
            members = entity.members + AccountMemberEntity.fromDomain(member)
        )

        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(entity))

        whenever(accountRepository.save(updatedEntity))
            .thenReturn(updatedEntity)

        // when
        val result = adapter.addAccountMember(
            TestConstants.ACCOUNT_ID,
            member
        )

        // then
        assertEquals(2, result.members.size)
        assertEquals(true, result.members.contains(member))

        verify(accountRepository).save(updatedEntity)
    }

    @Test
    fun `should throw AccountMemberNotFoundException when removing unknown member`() {
        // given
        val entity = AccountEntity.fromDomain(TestConstants.ACCOUNT)
        val userId = UUID.randomUUID()

        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(entity))

        // when & then
        assertThrows<AccountMemberNotFoundException> {
            adapter.removeMember(
                TestConstants.ACCOUNT_ID,
                userId
            )
        }

        verify(accountRepository, never()).save(org.mockito.kotlin.any())
    }
}
