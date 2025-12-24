package com.example.expense_management_server.integration.adapter.api

import com.example.expense_management_server.adapter.persistence.model.BalanceGroupEntity
import com.example.expense_management_server.adapter.persistence.model.ExpenseEntity
import com.example.expense_management_server.adapter.persistence.repository.BalanceGroupRepository
import com.example.expense_management_server.adapter.persistence.repository.ExpenseRepository
import com.example.expense_management_server.domain.expense.model.ExpenseSplitType
import com.example.expense_management_server.integration.IntegrationTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.Test

class BalanceGroupControllerTest : IntegrationTest() {

    @Autowired
    private lateinit var balanceGroupRepository: BalanceGroupRepository

    @Autowired
    private lateinit var expenseRepository: ExpenseRepository

    private lateinit var balanceGroupId: UUID
    private lateinit var expenseId: UUID

    @BeforeEach
    fun addBalanceGroupAndExpenses() {
        val user = userRepository.findByEmail(STANDARD_USER_EMAIL)!!
        val savedBalanceGroup = balanceGroupRepository.save(
            BalanceGroupEntity(
                id = null,
                createdBy = user,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                groupName = "test-balance-group",
                groupMembers = setOf(user),
            )
        )
        balanceGroupId = savedBalanceGroup.id!!
        val savedExpense = expenseRepository.save(
            ExpenseEntity(
                id = null,
                createdBy = user,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                name = "test-expense",
                amount = 10.0,
                balanceGroup = savedBalanceGroup,
                splitType = ExpenseSplitType.EQUALLY
            )
        )
        expenseId = savedExpense.id!!
    }

    @AfterEach
    fun deleteBalanceGroupAndExpenses() {
        expenseRepository.deleteAll()
        balanceGroupRepository.deleteAll()
    }

    @Test
    fun `when user is logged in then should save balance group`() {

    }

    @Test
    fun `when user is logged in and group member not exists then should return 400`() {

    }

    @Test
    fun `when user is balance group owner and data is correct then should update balance group`() {

    }

    @Test
    fun `when update balance group and user is not owner of balance group then should return 403`() {

    }

    @Test
    fun `when update balance group which was already updated then last updated timestamp should be changed`() {

    }

    @Test
    fun `when update not existing balance group then should return 404`() {

    }

    @Test
    fun `when user is balance group owner then should remove balance group and all related expenses`() {

    }

    @Test
    fun `when user is balance group member then should get balance group by id`() {

    }

    @Test
    fun `when get all balance groups then should return balance groups where user is a member`() {

    }

    @Test
    fun `when adding new expense and user is balance group member then expense should be added to balance group`() {

    }

    @Test
    fun `when adding new expense and user is not balance group member then should return 403`() {

    }

    @Test
    fun `when removing expense from balance group and user is expense owner then should be removed from balance group`() {

    }

    @Test
    fun `when removing expense from balance group and user is expense owner but not balance group member then expense should be removed from balance group`() {

    }

    @Test
    fun `when removing expense from balance group and user is not expense owner but balance group member then should be removed from balance group`() {

    }

    @Test
    fun `when user is balance group owner then should get all expenses from balance group`() {

    }

    @Test
    fun `when user is balance group member but not owner then should get all expenses from balance group`() {

    }

    @Test
    fun `when user is balance group owner then should get expense by Id`() {

    }

    @Test
    fun `when user is balance group member but not owner then should get expense by Id`() {

    }

    @Test
    fun `when user is not balance group member and not expense owner but expense exists in database then should return 404 when get expense by id`() {

    }

    @Test
    fun `when user is not balance group member and not expense owner but expense exists in database then should return 404 when delete expense`() {

    }

    @Test
    fun `when user is not balance group member and not expense owner but expense exists in database then should return 404 when update expense`() {

    }

    @Test
    fun `when user is not balance group member and not owner then should return 404 when get balance group by id`() {

    }

    @Test
    fun `when user is not balance group member and not owner then should return 404 when update balance group`() {

    }

    @Test
    fun `when user is not balance group member and not owner then should return 404 when delete balance group`() {

    }

    @Test
    fun `when logged user is admin then should create new balance group`() {

    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should update balance group`() {

    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should delete balance group`() {

    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should get balance group by Id`() {

    }

    @Test
    fun `when logged user is admin then should get all balance groups from database`() {

    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should get all expenses from balance group`() {

    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should add expense to balance group`() {

    }

    @Test
    fun `when logged user is admin and is not balance group member, owner and expense owner then should remove expense from balance group`() {

    }

    @Test
    fun `when logged user is admin and is not balance group member, owner and expense owner then should update expense in balance group`() {

    }
}