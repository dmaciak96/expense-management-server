package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.persistence.model.AccountEntity
import com.example.expense_management_server.adapter.persistence.repository.AccountRepository
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

class AccountPersistenceAdapterTest {

    private val accountRepository: AccountRepository = mock()
    private val accountPersistenceAdapter: AccountPersistenceAdapter = AccountPersistenceAdapter(accountRepository)

    @Test
    fun `when provided proper domain account object then should create new account`() {
        // given
        val accountEntity = AccountEntity.fromDomain(TestConstants.ACCOUNT)
        whenever(accountRepository.save(accountEntity)).thenReturn(accountEntity)

        // when
        val result = accountPersistenceAdapter.create(TestConstants.ACCOUNT)

        // then
        verify(accountRepository).save(accountEntity)
        assertThat(result, equalTo(TestConstants.ACCOUNT))
    }

    @Test
    fun `when account with provided name not exists then should throw AccountNotFoundException`() {
        // given
        whenever(accountRepository.findByName(TestConstants.ACCOUNT_NAME)).thenReturn(null)

        // when & then
        assertThrows<AccountNotFoundException> {
            accountPersistenceAdapter.findByName(TestConstants.ACCOUNT_NAME)
        }
        verify(accountRepository).findByName(TestConstants.ACCOUNT_NAME)
    }

    @Test
    fun `when account with provided name exists then should return proper account`() {
        // given
        whenever(accountRepository.findByName(TestConstants.ACCOUNT_NAME))
            .thenReturn(AccountEntity.fromDomain(TestConstants.ACCOUNT))

        // when
        val result = accountPersistenceAdapter.findByName(TestConstants.ACCOUNT_NAME)

        // then
        verify(accountRepository).findByName(TestConstants.ACCOUNT_NAME)
        assertThat(result, equalTo(TestConstants.ACCOUNT))
    }

    @Test
    fun `when user created account then should return proper object`() {
        // given
        whenever(accountRepository.findAllByCreatedById(TestConstants.USER_ONE_ID.toString()))
            .thenReturn(listOf(AccountEntity.fromDomain(TestConstants.ACCOUNT)))

        // when
        val result = accountPersistenceAdapter.findAllByCreatorId(TestConstants.USER_ONE_ID)

        // then
        verify(accountRepository).findAllByCreatedById(TestConstants.USER_ONE_ID.toString())
        assertThat(result, hasSize(1))
        assertThat(result, containsInAnyOrder(TestConstants.ACCOUNT))
    }

    @Test
    fun `when user not created account then should return empty list`() {
        // given
        whenever(accountRepository.findAllByCreatedById(TestConstants.USER_ONE_ID.toString()))
            .thenReturn(emptyList())

        // when
        val result = accountPersistenceAdapter.findAllByCreatorId(TestConstants.USER_ONE_ID)

        // then
        verify(accountRepository).findAllByCreatedById(TestConstants.USER_ONE_ID.toString())
        assertThat(result, hasSize(0))
    }

    @Test
    fun `when account with provided id not exists then should throw AccountNotFoundException`() {
        // given
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString())).thenReturn(Optional.empty())

        // when & then
        assertThrows<AccountNotFoundException> {
            accountPersistenceAdapter.findById(TestConstants.ACCOUNT_ID)
        }
        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
    }

    @Test
    fun `when account with provided id exists then should return proper account`() {
        // given
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(AccountEntity.fromDomain(TestConstants.ACCOUNT)))

        // when
        val result = accountPersistenceAdapter.findById(TestConstants.ACCOUNT_ID)

        // then
        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
        assertThat(result, equalTo(TestConstants.ACCOUNT))
    }

    @Test
    fun `when account exists then should add new expense`() {
        // given
        val newExpense = TestConstants.EXPENSE.copy(name = "New expense")
        val expected = TestConstants.ACCOUNT.copy(
            expenses = setOf(
                TestConstants.EXPENSE,
                newExpense
            )
        )
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(AccountEntity.fromDomain(TestConstants.ACCOUNT)))
        whenever(accountRepository.save(any())).thenReturn(AccountEntity.fromDomain(expected))

        // when
        val result = accountPersistenceAdapter.addExpense(newExpense, TestConstants.ACCOUNT_ID)

        // then
        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
        verify(accountRepository).save(AccountEntity.fromDomain(expected))
        assertThat(result, equalTo(expected))
    }

    @Test
    fun `when account and expense exists then should remove expense`() {
        // given
        val expected = TestConstants.ACCOUNT.copy(expenses = emptySet())
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(AccountEntity.fromDomain(TestConstants.ACCOUNT)))
        whenever(accountRepository.save(any())).thenReturn(AccountEntity.fromDomain(expected))

        // when
        val result = accountPersistenceAdapter.removeExpense(TestConstants.ACCOUNT_ID, TestConstants.EXPENSE.id)

        // then
        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
        verify(accountRepository).save(AccountEntity.fromDomain(expected))
        assertThat(result, equalTo(expected))
    }

    @Test
    fun `when account exists then should add new account member`() {
        // given
        val newAccountMember =
            TestConstants.APPLICATION_USER_ONE.copy(firstName = "Ned", lastName = "Stark").toAccountMember()
        val expected = TestConstants.ACCOUNT.copy(
            members = setOf(
                newAccountMember,
                TestConstants.APPLICATION_USER_ONE.toAccountMember(),
                TestConstants.APPLICATION_USER_TWO.toAccountMember()
            )
        )
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(AccountEntity.fromDomain(TestConstants.ACCOUNT)))
        whenever(accountRepository.save(any())).thenReturn(AccountEntity.fromDomain(expected))

        // when
        val result = accountPersistenceAdapter.addAccountMember(TestConstants.ACCOUNT_ID, newAccountMember)

        // then
        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
        verify(accountRepository).save(AccountEntity.fromDomain(expected))
        assertThat(result, equalTo(expected))
    }

    @Test
    fun `when account and expense exists then should remove account member`() {
        // given
        val expected = TestConstants.ACCOUNT.copy(members = setOf(TestConstants.APPLICATION_USER_ONE.toAccountMember()))
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(AccountEntity.fromDomain(TestConstants.ACCOUNT)))
        whenever(accountRepository.save(any())).thenReturn(AccountEntity.fromDomain(expected))

        // when
        val result = accountPersistenceAdapter.removeMember(TestConstants.ACCOUNT_ID, TestConstants.USER_TWO_ID)

        // then
        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
        verify(accountRepository).save(AccountEntity.fromDomain(expected))
        assertThat(result, equalTo(expected))
    }

    @Test
    fun `when account exists then should remove account`() {
        // given
        doNothing().`when`(accountRepository).deleteById(TestConstants.ACCOUNT_ID.toString())
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString()))
            .thenReturn(Optional.of(AccountEntity.fromDomain(TestConstants.ACCOUNT)))

        // when & then
        accountPersistenceAdapter.deleteById(TestConstants.ACCOUNT_ID)
        verify(accountRepository).deleteById(TestConstants.ACCOUNT_ID.toString())
        verify(accountRepository).findById(TestConstants.ACCOUNT_ID.toString())
    }

    @Test
    fun `when account not exists then should throw AccountNotFoundException`() {
        doNothing().`when`(accountRepository).deleteById(TestConstants.ACCOUNT_ID.toString())
        whenever(accountRepository.findById(TestConstants.ACCOUNT_ID.toString())).thenReturn(Optional.empty())

        // when & then
        assertThrows<AccountNotFoundException> { accountPersistenceAdapter.deleteById(TestConstants.ACCOUNT_ID) }
    }
}