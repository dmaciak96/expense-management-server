package com.example.expense_management_server.integration.adapter.api

import com.example.expense_management_server.adapter.api.model.BalanceGroupRequest
import com.example.expense_management_server.adapter.api.model.BalanceGroupResponse
import com.example.expense_management_server.adapter.api.model.ExpenseRequest
import com.example.expense_management_server.adapter.api.model.ExpenseResponse
import com.example.expense_management_server.adapter.persistence.model.BalanceGroupEntity
import com.example.expense_management_server.adapter.persistence.model.ExpenseEntity
import com.example.expense_management_server.adapter.persistence.model.UserEntity
import com.example.expense_management_server.adapter.persistence.repository.BalanceGroupRepository
import com.example.expense_management_server.adapter.persistence.repository.ExpenseRepository
import com.example.expense_management_server.adapter.security.model.UserAccount
import com.example.expense_management_server.domain.expense.model.ExpenseSplitType
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.integration.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.client.expectBody
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.Test

class BalanceGroupControllerTest : IntegrationTest() {

    @Autowired
    private lateinit var balanceGroupRepository: BalanceGroupRepository

    @Autowired
    private lateinit var expenseRepository: ExpenseRepository

    private lateinit var adminUserId: UUID

    private lateinit var standardUserId: UUID
    private lateinit var standardUserExpenseId: UUID
    private lateinit var standardUserBalanceGroupId: UUID

    private lateinit var additionalUserId: UUID
    private lateinit var additionalUserExpenseId: UUID
    private lateinit var additionalUserBalanceGroupId: UUID


    @BeforeEach
    fun setup() {
        val user = userRepository.findByEmail(STANDARD_USER_EMAIL) ?: throw UserNotFoundException()
        val secondUser = userRepository.findByEmail(ADDITIONAL_USER_EMAIL) ?: throw UserNotFoundException()
        setSecurityContext(user)

        val primaryBalanceGroup = balanceGroupRepository.save(
            BalanceGroupEntity(
                id = null,
                createdById = user.id,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                groupName = GROUP_NAME,
                groupMembers = setOf(user, secondUser),
            )
        )

        val standardUserExpense = expenseRepository.save(
            ExpenseEntity(
                id = null,
                createdById = user.id,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                name = EXPENSE_NAME,
                amount = AMOUNT,
                balanceGroup = primaryBalanceGroup,
                splitType = ExpenseSplitType.EQUALLY
            )
        )

        setSecurityContext(secondUser)
        val additionalUserExpense = expenseRepository.save(
            ExpenseEntity(
                id = null,
                createdById = secondUser.id,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                name = SECONDARY_EXPENSE_NAME,
                amount = SECONDARY_AMOUNT,
                balanceGroup = primaryBalanceGroup,
                splitType = ExpenseSplitType.EQUALLY
            )
        )

        val secondaryBalanceGroup = balanceGroupRepository.save(
            BalanceGroupEntity(
                id = null,
                createdById = secondUser.id,
                createdAt = OffsetDateTime.now(),
                updatedAt = null,
                groupName = SECONDARY_GROUP_NAME,
                groupMembers = setOf(secondUser),
            )
        )

        standardUserBalanceGroupId = primaryBalanceGroup.id ?: throw RuntimeException()
        additionalUserBalanceGroupId = secondaryBalanceGroup.id ?: throw RuntimeException()
        standardUserExpenseId = standardUserExpense.id ?: throw RuntimeException()
        additionalUserExpenseId = additionalUserExpense.id ?: throw RuntimeException()
        adminUserId = userRepository.findByEmail(ADMIN_USER_EMAIL)!!.id ?: throw RuntimeException()
        standardUserId = user.id ?: throw RuntimeException()
        additionalUserId = secondUser.id ?: throw RuntimeException()
    }

    @AfterEach
    fun deleteBalanceGroupAndExpenses() {
        expenseRepository.deleteAll()
        balanceGroupRepository.deleteAll()
    }

    @Test
    fun `when user is logged in then should save balance group`() {
        restTestClient.post()
            .uri("/balance-groups")
            .body(
                BalanceGroupRequest(
                    groupName = UPDATED_GROUP_NAME,
                    groupMemberIds = emptyList()
                )
            )
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isCreated
            .expectBody<BalanceGroupResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isNotNull()
                assertThat(responseBody.groupName).isEqualTo(UPDATED_GROUP_NAME)
                assertThat(responseBody.groupMemberIds).containsOnly(standardUserId)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
            }
    }

    @Test
    fun `when user is logged in and group member not exists then should return 400`() {
        restTestClient.post()
            .uri("/balance-groups")
            .body(
                BalanceGroupRequest(
                    groupName = UPDATED_GROUP_NAME,
                    groupMemberIds = listOf(NOT_EXISTING_USER_ID)
                )
            )
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isBadRequest
            .expectBody<ProblemDetail>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.status).isEqualTo(400)
                assertThat(responseBody.detail).isEqualTo("Some of the members have not been found")
            }
    }

    @Test
    fun `when user is balance group owner and data is correct then should update balance group`() {
        restTestClient.put()
            .uri("/balance-groups/$standardUserBalanceGroupId")
            .body(
                BalanceGroupRequest(
                    groupName = UPDATED_GROUP_NAME,
                    groupMemberIds = listOf(additionalUserId)
                )
            )
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<BalanceGroupResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isNotNull()
                assertThat(responseBody.groupName).isEqualTo(UPDATED_GROUP_NAME)
                assertThat(responseBody.groupMemberIds).contains(standardUserId, additionalUserId)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNotNull()
            }
    }

    @Test
    fun `when update balance group and user is not owner of balance group then should return 403`() {
        restTestClient.put()
            .uri("/balance-groups/$standardUserBalanceGroupId")
            .body(
                BalanceGroupRequest(
                    groupName = UPDATED_GROUP_NAME,
                    groupMemberIds = listOf(additionalUserId)
                )
            )
            .headers { it.setBasicAuth(ADDITIONAL_USER_EMAIL, ADDITIONAL_USER_PASSWORD) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when update not existing balance group then should return 404`() {
        restTestClient.put()
            .uri("/balance-groups/$NOT_EXISTING_BALANCE_GROUP_ID")
            .body(
                BalanceGroupRequest(
                    groupName = UPDATED_GROUP_NAME,
                    groupMemberIds = listOf(additionalUserId)
                )
            )
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `when user is balance group owner then should remove balance group and all related expenses`() {
        restTestClient.delete()
            .uri("/balance-groups/$standardUserBalanceGroupId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isNoContent

        assertThat(expenseRepository.count()).isZero()
        assertThat(balanceGroupRepository.count()).isEqualTo(1) // secondary user also have 1 balance group
    }

    @Test
    fun `when user is balance group member then should get balance group by id`() {
        restTestClient.get()
            .uri("/balance-groups/$standardUserBalanceGroupId")
            .headers { it.setBasicAuth(ADDITIONAL_USER_EMAIL, ADDITIONAL_USER_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<BalanceGroupResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(standardUserBalanceGroupId)
                assertThat(responseBody.groupName).isEqualTo(GROUP_NAME)
                assertThat(responseBody.groupMemberIds).containsOnly(standardUserId, additionalUserId)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
            }
    }

    @Test
    fun `when get all balance groups then should return balance groups where user is a member`() {
        restTestClient.get()
            .uri("/balance-groups")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<List<BalanceGroupResponse>>()
            .consumeWith {
                assertThat(it.responseBody!!.size).isEqualTo(1)
                val responseBody = it.responseBody!![0]
                assertThat(responseBody.id).isEqualTo(standardUserBalanceGroupId)
                assertThat(responseBody.groupName).isEqualTo(GROUP_NAME)
                assertThat(responseBody.groupMemberIds).containsOnly(standardUserId, additionalUserId)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
            }

        restTestClient.get()
            .uri("/balance-groups")
            .headers { it.setBasicAuth(ADDITIONAL_USER_EMAIL, ADDITIONAL_USER_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<List<BalanceGroupResponse>>()
            .consumeWith {
                assertThat(it.responseBody!!.size).isEqualTo(2)
            }
    }

    @Test
    fun `when adding new expense and user is balance group member then expense should be added to balance group`() {
        restTestClient.post()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses")
            .headers { it.setBasicAuth(ADDITIONAL_USER_EMAIL, ADDITIONAL_USER_PASSWORD) }
            .body(
                ExpenseRequest(
                    name = "new-expense",
                    amount = 20.0,
                    splitType = ExpenseSplitType.EQUALLY
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody<ExpenseResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isNotNull()
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
                assertThat(responseBody.expenseOwnerId).isEqualTo(additionalUserId)
                assertThat(responseBody.amount).isEqualTo(20.0)
                assertThat(responseBody.name).isEqualTo("new-expense")
                assertThat(responseBody.splitType).isEqualTo(ExpenseSplitType.EQUALLY)
            }
    }

    @Test
    fun `when adding new expense and user is not balance group member then should return 403`() {
        restTestClient.post()
            .uri("/balance-groups/$additionalUserBalanceGroupId/expenses")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .body(
                ExpenseRequest(
                    name = "new-expense",
                    amount = 20.0,
                    splitType = ExpenseSplitType.EQUALLY
                )
            )
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when removing expense from balance group and user is expense owner then should be removed from balance group`() {
        restTestClient.delete()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses/$additionalUserExpenseId")
            .headers { it.setBasicAuth(ADDITIONAL_USER_EMAIL, ADDITIONAL_USER_PASSWORD) }
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `when user is balance group owner then should get all expenses from balance group`() {
        restTestClient.get()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<List<ExpenseResponse>>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.size).isEqualTo(2)
            }
    }

    @Test
    fun `when user is balance group member but not owner then should get all expenses from balance group`() {
        restTestClient.get()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses")
            .headers { it.setBasicAuth(ADDITIONAL_USER_EMAIL, ADDITIONAL_USER_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<List<ExpenseResponse>>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.size).isEqualTo(2)
            }
    }

    @Test
    fun `when user is balance group owner then should get expense by Id`() {
        restTestClient.get()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses/$standardUserExpenseId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<ExpenseResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(standardUserExpenseId)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
                assertThat(responseBody.expenseOwnerId).isEqualTo(standardUserId)
                assertThat(responseBody.amount).isEqualTo(AMOUNT)
                assertThat(responseBody.name).isEqualTo(EXPENSE_NAME)
                assertThat(responseBody.splitType).isEqualTo(ExpenseSplitType.EQUALLY)
            }
    }

    @Test
    fun `when user is balance group member but not owner then should get expense by Id`() {
        restTestClient.get()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses/$additionalUserExpenseId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<ExpenseResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(additionalUserExpenseId)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
                assertThat(responseBody.expenseOwnerId).isEqualTo(additionalUserId)
                assertThat(responseBody.amount).isEqualTo(SECONDARY_AMOUNT)
                assertThat(responseBody.name).isEqualTo(SECONDARY_EXPENSE_NAME)
                assertThat(responseBody.splitType).isEqualTo(ExpenseSplitType.EQUALLY)
            }
    }

    @Test
    fun `when user is not balance group member and not expense owner but expense exists in database then should return 403 when get expense by id`() {
        restTestClient.get()
            .uri("/balance-groups/$additionalUserBalanceGroupId/expenses/$standardUserExpenseId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when user is not balance group member and not expense owner but expense exists in database then should return 403 when delete expense`() {
        restTestClient.delete()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses/$additionalUserExpenseId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when user is not balance group member and not expense owner but expense exists in database then should return 403 when update expense`() {
        restTestClient.put()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses/$additionalUserExpenseId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .body(
                ExpenseRequest(
                    name = "updated-expense",
                    amount = 123.0,
                    splitType = ExpenseSplitType.EQUALLY
                )
            )
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when user is not balance group member and not owner then should return 403 when get balance group by id`() {
        restTestClient.get()
            .uri("/balance-groups/$additionalUserBalanceGroupId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when user is not balance group member and not owner then should return 403 when update balance group`() {
        restTestClient.put()
            .uri("/balance-groups/$additionalUserBalanceGroupId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .body(
                BalanceGroupRequest(
                    groupName = UPDATED_GROUP_NAME,
                    groupMemberIds = emptyList()
                )
            )
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when user is not balance group member and not owner then should return 403 when delete balance group`() {
        restTestClient.delete()
            .uri("/balance-groups/$additionalUserBalanceGroupId")
            .headers { it.setBasicAuth(STANDARD_USER_EMAIL, STANDARD_PASSWORD) }
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `when logged user is admin then should create new balance group`() {
        restTestClient.post()
            .uri("/balance-groups")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .body(
                BalanceGroupRequest(
                    groupName = UPDATED_GROUP_NAME,
                    groupMemberIds = emptyList()
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody<BalanceGroupResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isNotNull()
                assertThat(responseBody.groupName).isEqualTo(UPDATED_GROUP_NAME)
                assertThat(responseBody.groupMemberIds).containsOnly(adminUserId)
                assertThat(responseBody.createdAt).isNotNull()
                assertThat(responseBody.updatedAt).isNull()
            }
    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should delete balance group`() {
        restTestClient.delete()
            .uri("/balance-groups/$standardUserBalanceGroupId")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .exchange()
            .expectStatus().isNoContent

        assertThat(expenseRepository.count()).isZero()
        assertThat(balanceGroupRepository.count()).isEqualTo(1)
    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should get balance group by Id`() {
        restTestClient.get()
            .uri("/balance-groups/$standardUserBalanceGroupId")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<BalanceGroupResponse>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.id).isEqualTo(standardUserBalanceGroupId)
                assertThat(responseBody.groupName).isEqualTo(GROUP_NAME)
                assertThat(responseBody.groupMemberIds).containsOnly(standardUserId, additionalUserId)
                assertThat(responseBody.createdAt).isNotNull()
            }
    }

    @Test
    fun `when logged user is admin and is not balance group member and owner then should get all expenses from balance group`() {
        restTestClient.get()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .exchange()
            .expectStatus().isOk
            .expectBody<List<ExpenseResponse>>()
            .consumeWith {
                val responseBody = it.responseBody!!
                assertThat(responseBody.size).isEqualTo(2)
            }
    }

    @Test
    fun `when logged user is admin and is not balance group member, owner and expense owner then should remove expense from balance group`() {
        restTestClient.delete()
            .uri("/balance-groups/$standardUserBalanceGroupId/expenses/$additionalUserExpenseId")
            .headers { it.setBasicAuth(ADMIN_USER_EMAIL, ADMIN_PASSWORD) }
            .exchange()
            .expectStatus().isNoContent

        assertThat(expenseRepository.existsById(additionalUserExpenseId)).isFalse()
    }


    private fun setSecurityContext(user: UserEntity) {
        val principal = UserAccount(
            id = user.id!!,
            email = user.email,
            passwordHash = user.passwordHash,
            role = user.role
        )
        val auth = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
        SecurityContextHolder.getContext().authentication = auth
    }

    companion object {
        private const val UPDATED_GROUP_NAME = "updated-test-balance-group"
        private const val GROUP_NAME = "test-balance-group"
        private const val SECONDARY_GROUP_NAME = "sec-test-balance-group"
        private const val EXPENSE_NAME = "test-expense"
        private const val SECONDARY_EXPENSE_NAME = "sec-test-expense"
        private const val AMOUNT = 100.0
        private const val SECONDARY_AMOUNT = 50.0
        private val NOT_EXISTING_USER_ID = UUID.fromString("72f979a9-a930-4bcb-8996-313ad44a7772")
        private val NOT_EXISTING_BALANCE_GROUP_ID = UUID.fromString("72f979a9-a930-4bcb-8996-313ad44a6662")
    }
}