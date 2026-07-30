package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.AccountEntity
import com.example.expense_management_server.adapter.persistence.model.AccountMemberEntity
import com.example.expense_management_server.adapter.persistence.model.ExpenseEntity
import com.example.expense_management_server.adapter.persistence.repository.AccountRepository
import com.example.expense_management_server.domain.account.exception.AccountMemberNotFoundException
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountMember
import com.example.expense_management_server.domain.account.model.Expense
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountPersistenceAdapter(
    private val accountRepository: AccountRepository,
) : AccountPersistencePort {
    override fun create(account: Account): Account {
        LOGGER.debug { "Saving new account in DB $account" }
        val savedEntity = accountRepository.save(AccountEntity.fromDomain(account))
        val savedDomain = savedEntity.toDomain()
        LOGGER.debug { "Saved new account in DB $savedDomain" }
        return savedDomain
    }

    override fun findByName(name: String): Account {
        LOGGER.debug { "Searching account by name: $name" }
        val accountEntity =
            accountRepository.findByName(name) ?: throw AccountNotFoundException("Account with name $name not found")
        val accountDomain = accountEntity.toDomain()
        LOGGER.debug { "Account founded by it's name: $accountDomain" }
        return accountDomain
    }

    override fun findById(id: UUID): Account {
        return accountRepository.findById(id.toString())
            .map {
                val domain = it.toDomain()
                LOGGER.debug { "Account founded by it's ID: $domain" }
                return@map domain
            }
            .orElseThrow { AccountNotFoundException("Account not found with specified ID: $id") }
    }

    override fun findAllByCreatorId(userId: UUID): List<Account> {
        LOGGER.debug { "Searching accounts created by user: $userId" }
        return accountRepository.findAllByCreatedById(userId.toString())
            .map { it.toDomain() }
    }

    override fun findAllByMemberId(userId: UUID): List<Account> {
        LOGGER.debug { "Searching accounts where user $userId is member" }
        return accountRepository.findAll()
            .filter { accountEntity ->
                accountEntity.members.map { member -> member.applicationUserId }.contains(userId.toString())
            }
            .map { it.toDomain() }
    }

    override fun update(account: Account): Account {
        accountRepository.findById(account.id.toString())
            .orElseThrow { AccountNotFoundException("Account not found with specified ID: ${account.id}") }
        LOGGER.debug { "Updating account in DB with new data: $account" }
        val updatedEntity = accountRepository.save(AccountEntity.fromDomain(account))
        val domain = updatedEntity.toDomain()
        LOGGER.debug { "Account updated successfully $domain" }
        return domain
    }

    override fun deleteById(id: UUID) {
        accountRepository.findById(id.toString())
            .orElseThrow { AccountNotFoundException("Account not found with specified ID: $id") }
        LOGGER.debug { "Deleting account $id from DB" }
        accountRepository.deleteById(id.toString())
        LOGGER.debug { "Account $id was deleted" }
    }

    override fun addExpense(
        expense: Expense,
        accountId: UUID
    ): Account {
        val accountEntity = accountRepository.findById(accountId.toString())
            .orElseThrow { AccountNotFoundException("Account not found with specified ID: $accountId") }
        LOGGER.debug { "Adding expense $expense to account: ${accountEntity.toDomain()}" }
        val newExpensesSet = accountEntity.expenses + ExpenseEntity.fromDomain(expense)
        val updatedEntity = accountRepository.save(accountEntity.copy(expenses = newExpensesSet))
        val domain = updatedEntity.toDomain()
        LOGGER.debug { "Account updated successfully: $domain" }
        return domain
    }

    override fun removeExpense(
        accountId: UUID,
        expenseId: UUID
    ): Account {
        val accountEntity = accountRepository.findById(accountId.toString())
            .orElseThrow { AccountNotFoundException("Account not found with specified ID: $accountId") }

        LOGGER.debug { "Removing expense $expenseId from account: ${accountEntity.toDomain()}" }
        val newExpensesSet = accountEntity.expenses.toMutableSet()
        val isRemoved = newExpensesSet.removeIf { it.id == expenseId.toString() }
        if (!isRemoved) {
            throw ExpenseNotFoundException("Expense not found with specified ID: $expenseId")
        }
        val updatedEntity = accountRepository.save(accountEntity.copy(expenses = newExpensesSet))
        val domain = updatedEntity.toDomain()
        LOGGER.debug { "Account updated successfully: $domain" }
        return domain
    }

    override fun addAccountMember(
        accountId: UUID,
        accountMember: AccountMember
    ): Account {
        val accountEntity = accountRepository.findById(accountId.toString())
            .orElseThrow { AccountNotFoundException("Account not found with specified ID: $accountId") }

        LOGGER.debug { "Adding account member $accountMember. to account: ${accountEntity.toDomain()}" }
        val newMembersSet = accountEntity.members + AccountMemberEntity.fromDomain(accountMember)
        val updatedEntity = accountRepository.save(accountEntity.copy(members = newMembersSet))
        val domain = updatedEntity.toDomain()
        LOGGER.debug { "Account updated successfully: $domain" }
        return domain
    }

    override fun removeMember(
        accountId: UUID,
        userId: UUID
    ): Account {
        val accountEntity = accountRepository.findById(accountId.toString())
            .orElseThrow { AccountNotFoundException("Account not found with specified ID: $accountId") }

        LOGGER.debug { "Removing account member $userId from account: ${accountEntity.toDomain()}" }
        val newMembersSet = accountEntity.members.toMutableSet()
        val isRemoved = newMembersSet.removeIf { it.applicationUserId == userId.toString() }
        if (!isRemoved) {
            throw AccountMemberNotFoundException("Account member not found with specified ID: $userId")
        }
        val updatedEntity = accountRepository.save(accountEntity.copy(members = newMembersSet))
        val domain = updatedEntity.toDomain()
        LOGGER.debug { "Account updated successfully: $domain" }
        return domain
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}