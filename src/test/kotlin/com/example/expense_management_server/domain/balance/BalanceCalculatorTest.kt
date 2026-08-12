package com.example.expense_management_server.domain.balance

import com.example.expense_management_server.domain.account.model.Account
import com.example.expense_management_server.domain.account.model.AccountStatus
import com.example.expense_management_server.domain.account.model.Currency
import com.example.expense_management_server.domain.account.model.Expense
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.model.ApplicationUserStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

class BalanceCalculatorTest {

    private val balanceCalculator = BalanceCalculator()

    @Nested
    inner class TwoUsers {

        private val owner = createUser("owner@test.com")
        private val member = createUser("member@test.com")

        @Test
        fun `should return zero when account has no expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member),
                expenses = emptyList()
            )

            assertEquals(
                0L,
                balanceCalculator.calculateBalance(account, owner.id)
            )

            assertEquals(
                0L,
                balanceCalculator.calculateBalance(account, member.id)
            )
        }

        @Test
        fun `should return positive balance when owner paid all expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member),
                expenses = listOf(
                    createExpense(owner, 10_000L)
                )
            )

            assertEquals(
                5_000L,
                balanceCalculator.calculateBalance(account, owner.id)
            )
        }

        @Test
        fun `should return negative balance when member did not pay anything`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member),
                expenses = listOf(
                    createExpense(owner, 10_000L)
                )
            )

            assertEquals(
                -5_000L,
                balanceCalculator.calculateBalance(account, member.id)
            )
        }

        @Test
        fun `should calculate balance when both users paid different amounts`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member),
                expenses = listOf(
                    createExpense(owner, 10_000L),
                    createExpense(member, 2_000L)
                )
            )

            assertEquals(
                4_000L,
                balanceCalculator.calculateBalance(account, owner.id)
            )

            assertEquals(
                -4_000L,
                balanceCalculator.calculateBalance(account, member.id)
            )
        }

        @Test
        fun `should return zero when both users paid equal amounts`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member),
                expenses = listOf(
                    createExpense(owner, 10_000L),
                    createExpense(member, 10_000L)
                )
            )

            assertEquals(
                0L,
                balanceCalculator.calculateBalance(account, owner.id)
            )

            assertEquals(
                0L,
                balanceCalculator.calculateBalance(account, member.id)
            )
        }

        @Test
        fun `sum of balances should be zero when amount cannot be divided equally`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member),
                expenses = listOf(
                    createExpense(owner, 10_001L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero for multiple expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member),
                expenses = listOf(
                    createExpense(owner, 10_001L),
                    createExpense(member, 5_000L),
                    createExpense(owner, 999L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }
    }

    @Nested
    inner class ThreeUsers {

        private val owner = createUser("owner@test.com")
        private val member1 = createUser("member1@test.com")
        private val member2 = createUser("member2@test.com")

        @Test
        fun `should return zero when account has no expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = emptyList()
            )

            assertEquals(
                listOf(0L, 0L, 0L),
                calculateAllBalances(account)
            )
        }

        @Test
        fun `should calculate balances when owner paid all expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = listOf(
                    createExpense(owner, 9_000L)
                )
            )

            assertEquals(
                6_000L,
                balanceCalculator.calculateBalance(account, owner.id)
            )

            assertEquals(
                -3_000L,
                balanceCalculator.calculateBalance(account, member1.id)
            )

            assertEquals(
                -3_000L,
                balanceCalculator.calculateBalance(account, member2.id)
            )
        }

        @Test
        fun `should calculate balances when multiple users paid expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = listOf(
                    createExpense(owner, 12_000L),
                    createExpense(member1, 6_000L)
                )
            )

            assertEquals(
                6_000L,
                balanceCalculator.calculateBalance(account, owner.id)
            )

            assertEquals(
                0L,
                balanceCalculator.calculateBalance(account, member1.id)
            )

            assertEquals(
                -6_000L,
                balanceCalculator.calculateBalance(account, member2.id)
            )
        }

        @Test
        fun `should return zero when all users paid equal amounts`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = listOf(
                    createExpense(owner, 5_000L),
                    createExpense(member1, 5_000L),
                    createExpense(member2, 5_000L)
                )
            )

            assertEquals(
                listOf(0L, 0L, 0L),
                calculateAllBalances(account)
            )
        }

        @Test
        fun `sum of balances should be zero when amount leaves remainder one`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = listOf(
                    createExpense(owner, 10_000L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero when amount leaves remainder two`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = listOf(
                    createExpense(owner, 10_001L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero for multiple non divisible expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = listOf(
                    createExpense(owner, 10_000L),
                    createExpense(member1, 5_005L),
                    createExpense(member2, 1_001L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero when every user has multiple expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2),
                expenses = listOf(
                    createExpense(owner, 12_345L),
                    createExpense(owner, 999L),
                    createExpense(member1, 5_001L),
                    createExpense(member1, 2_222L),
                    createExpense(member2, 777L),
                    createExpense(member2, 101L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }
    }

    @Nested
    inner class FourUsers {

        private val owner = createUser("owner@test.com")
        private val member1 = createUser("member1@test.com")
        private val member2 = createUser("member2@test.com")
        private val member3 = createUser("member3@test.com")

        @Test
        fun `should return zero when account has no expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = emptyList()
            )

            assertEquals(
                listOf(0L, 0L, 0L, 0L),
                calculateAllBalances(account)
            )
        }

        @Test
        fun `should calculate balances when owner paid all expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 20_000L)
                )
            )

            assertEquals(
                15_000L,
                balanceCalculator.calculateBalance(account, owner.id)
            )

            assertEquals(
                -5_000L,
                balanceCalculator.calculateBalance(account, member1.id)
            )

            assertEquals(
                -5_000L,
                balanceCalculator.calculateBalance(account, member2.id)
            )

            assertEquals(
                -5_000L,
                balanceCalculator.calculateBalance(account, member3.id)
            )
        }

        @Test
        fun `should calculate balances when different users paid different amounts`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 20_000L),
                    createExpense(member1, 10_000L),
                    createExpense(member2, 10_000L)
                )
            )

            assertEquals(
                10_000L,
                balanceCalculator.calculateBalance(account, owner.id)
            )

            assertEquals(
                0L,
                balanceCalculator.calculateBalance(account, member1.id)
            )

            assertEquals(
                0L,
                balanceCalculator.calculateBalance(account, member2.id)
            )

            assertEquals(
                -10_000L,
                balanceCalculator.calculateBalance(account, member3.id)
            )
        }

        @Test
        fun `should return zero when all users paid equal amounts`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 10_000L),
                    createExpense(member1, 10_000L),
                    createExpense(member2, 10_000L),
                    createExpense(member3, 10_000L)
                )
            )

            assertEquals(
                listOf(0L, 0L, 0L, 0L),
                calculateAllBalances(account)
            )
        }

        @Test
        fun `sum of balances should be zero when amount leaves remainder one`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 10_001L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero when amount leaves remainder two`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 10_002L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero when amount leaves remainder three`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 10_003L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero for complex expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 50_001L),
                    createExpense(member1, 12_345L),
                    createExpense(member2, 7_777L),
                    createExpense(member3, 999L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }

        @Test
        fun `sum of balances should be zero when every user has multiple expenses`() {
            val account = createAccount(
                owner = owner,
                members = listOf(member1, member2, member3),
                expenses = listOf(
                    createExpense(owner, 13_337L),
                    createExpense(owner, 101L),
                    createExpense(member1, 7_777L),
                    createExpense(member1, 2_001L),
                    createExpense(member2, 3_333L),
                    createExpense(member2, 555L),
                    createExpense(member3, 999L),
                    createExpense(member3, 123L)
                )
            )

            assertEquals(
                0L,
                calculateAllBalances(account).sum()
            )
        }
    }

    private fun calculateAllBalances(account: Account): List<Long> {
        val userIds = listOf(account.createdBy.id) +
                account.members.map { it.applicationUserId }

        return userIds.map {
            balanceCalculator.calculateBalance(
                account = account,
                applicationUserId = it
            )
        }
    }

    private fun createAccount(
        owner: ApplicationUser,
        members: List<ApplicationUser>,
        expenses: List<Expense>
    ) = Account(
        id = UUID.randomUUID(),
        createdAt = Instant.EPOCH,
        lastUpdatedAt = Instant.EPOCH,
        createdBy = owner,
        name = "Test account",
        currency = Currency.PLN,
        members = members
            .map { it.toAccountMember() }
            .toSet(),
        expenses = expenses.toSet(),
        status = AccountStatus.ACTIVE
    )

    private fun createExpense(
        paidBy: ApplicationUser,
        monetaryAmount: Long
    ) = Expense(
        id = UUID.randomUUID(),
        createdAt = Instant.EPOCH,
        lastUpdatedAt = Instant.EPOCH,
        createdBy = paidBy,
        paidBy = paidBy,
        name = "Test expense",
        monetaryAmount = monetaryAmount
    )

    private fun createUser(email: String) = ApplicationUser(
        id = UUID.randomUUID(),
        createdAt = Instant.EPOCH,
        lastUpdatedAt = Instant.EPOCH,
        firstName = "Test",
        lastName = "User",
        email = email,
        phoneNumber = null,
        password = "password",
        displayName = null,
        avatarUrl = null,
        status = ApplicationUserStatus.ACTIVE
    )
}