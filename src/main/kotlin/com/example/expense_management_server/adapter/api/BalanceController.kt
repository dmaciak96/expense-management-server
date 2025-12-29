package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.adapter.api.model.BalanceGroupRequest
import com.example.expense_management_server.adapter.api.model.BalanceGroupResponse
import com.example.expense_management_server.adapter.api.model.ExpenseRequest
import com.example.expense_management_server.adapter.api.model.ExpenseResponse
import com.example.expense_management_server.domain.balance.exception.BalanceGroupNotFoundException
import com.example.expense_management_server.domain.balance.exception.BalanceGroupValidationException
import com.example.expense_management_server.domain.balance.model.BalanceGroup
import com.example.expense_management_server.domain.expense.exception.ExpenseNotFoundException
import com.example.expense_management_server.domain.expense.exception.ExpenseValidationException
import com.example.expense_management_server.domain.expense.model.Expense
import com.example.expense_management_server.domain.facade.IBalanceManagementFacade
import com.example.expense_management_server.domain.facade.IExpenseManagementFacade
import com.example.expense_management_server.domain.user.port.ISecurityPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.util.UUID

@RestController
@RequestMapping("/balance-groups")
class BalanceGroupController(
    private val balanceGroupFacade: IBalanceManagementFacade,
    private val expenseFacade: IExpenseManagementFacade,
    private val securityPort: ISecurityPort
) {

    @GetMapping("/{balanceGroupId}")
    @PreAuthorize(IS_GROUP_MEMBER_OR_ADMIN_MATCHER)
    fun getById(@PathVariable balanceGroupId: UUID): BalanceGroupResponse {
        LOGGER.info { "HTTP request received: fetch balance group by id $balanceGroupId " }
        val balanceGroup = balanceGroupFacade.getById(balanceGroupId)
        val balance = balanceGroupFacade.calculateBalance(balanceGroup.id!!, securityPort.getCurrentLoginUserId())
        return BalanceGroupResponse.from(balanceGroup, balance)
    }

    @GetMapping
    fun getAllWhereUserIsMember(): List<BalanceGroupResponse> {
        LOGGER.info { "HTTP request received: fetch Balance groups" }
        return balanceGroupFacade.getAllWhereUserIsGroupMember(securityPort.getCurrentLoginUserId())
            .map {
                val balance = balanceGroupFacade.calculateBalance(it.id!!, securityPort.getCurrentLoginUserId())
                BalanceGroupResponse.from(it, balance)
            }
    }

    @DeleteMapping("/{balanceGroupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(IS_OWNER_OR_ADMIN_MATCHER)
    fun deleteById(@PathVariable balanceGroupId: UUID) {
        LOGGER.info { "HTTP request received: delete balance group $balanceGroupId " }
        balanceGroupFacade.delete(balanceGroupId)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun save(@RequestBody balanceGroupRequest: BalanceGroupRequest): BalanceGroupResponse {
        LOGGER.info { "HTTP request received: creating new balance group ${balanceGroupRequest.groupName}" }
        val balanceGroup = balanceGroupFacade.save(mapBalanceGroup(balanceGroupRequest))
        val balance = balanceGroupFacade.calculateBalance(balanceGroup.id!!, securityPort.getCurrentLoginUserId())
        return BalanceGroupResponse.from(balanceGroup, balance)
    }

    @PutMapping("/{balanceGroupId}")
    @PreAuthorize(IS_OWNER_OR_ADMIN_MATCHER)
    fun update(
        @PathVariable balanceGroupId: UUID,
        @RequestBody balanceGroupRequest: BalanceGroupRequest
    ): BalanceGroupResponse {
        LOGGER.info { "HTTP request received: updating balance group $balanceGroupId" }
        val balanceGroup = balanceGroupFacade.update(balanceGroupId, mapBalanceGroup(balanceGroupRequest))
        val balance = balanceGroupFacade.calculateBalance(balanceGroup.id!!, securityPort.getCurrentLoginUserId())
        return BalanceGroupResponse.from(balanceGroup, balance)
    }

    // --------------------------------EXPENSES--------------------------------
    @GetMapping("/{balanceGroupId}/expenses")
    @PreAuthorize(IS_GROUP_MEMBER_OR_ADMIN_MATCHER)
    fun getExpensesByGroup(@PathVariable balanceGroupId: UUID): List<ExpenseResponse> {
        LOGGER.info { "HTTP request received: fetch expenses by balance group $balanceGroupId" }
        return expenseFacade.getAllByBalanceGroup(balanceGroupId)
            .map { ExpenseResponse.from(it) }
    }

    @GetMapping("/{balanceGroupId}/expenses/{expenseId}")
    @PreAuthorize(IS_GROUP_MEMBER_OR_ADMIN_MATCHER)
    fun getExpensesById(
        @PathVariable balanceGroupId: UUID,
        @PathVariable expenseId: UUID
    ): ExpenseResponse {
        LOGGER.info { "HTTP request received: fetch expense $expenseId" }
        val expenseDomainModel = expenseFacade.getById(expenseId)
        if (expenseDomainModel.balanceGroupId != balanceGroupId) {
            throw ExpenseNotFoundException(expenseId)
        }
        return ExpenseResponse.from(expenseDomainModel)
    }

    @DeleteMapping("/{balanceGroupId}/expenses/{expenseId}")
    @PreAuthorize(IS_EXPENSE_OWNER_OR_ADMIN_MATCHER)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeExpenseFromBalanceGroup(
        @PathVariable balanceGroupId: UUID,
        @PathVariable expenseId: UUID
    ) {
        LOGGER.info { "HTTP request received: delete expense $expenseId from balance group $balanceGroupId" }
        val expenseDomainModel = expenseFacade.getById(expenseId)
        if (expenseDomainModel.balanceGroupId != balanceGroupId) {
            throw ExpenseNotFoundException(expenseId)
        }
        expenseFacade.delete(expenseId)
    }

    @PostMapping(
        "/{balanceGroupId}/expenses"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(IS_GROUP_MEMBER_OR_ADMIN_MATCHER)
    fun addExpenseToBalanceGroup(
        @PathVariable balanceGroupId: UUID,
        @RequestBody expenseRequest: ExpenseRequest
    ): ExpenseResponse {
        LOGGER.info { "HTTP request received: add new expense to balance group $balanceGroupId" }
        val savedExpense = expenseFacade.save(mapExpense(expenseRequest, balanceGroupId))
        return ExpenseResponse.from(savedExpense)
    }

    @PutMapping("/{balanceGroupId}/expenses/{expenseId}")
    @PreAuthorize(IS_EXPENSE_OWNER_OR_ADMIN_MATCHER)
    fun updateExpense(
        @PathVariable balanceGroupId: UUID,
        @PathVariable expenseId: UUID,
        @RequestBody expenseRequest: ExpenseRequest
    ): ExpenseResponse {
        LOGGER.info { "HTTP request received: update expense $expenseId" }
        val updatedExpense = expenseFacade.update(expenseId, mapExpense(expenseRequest, balanceGroupId))
        return ExpenseResponse.from(updatedExpense)
    }

    private fun mapBalanceGroup(balanceGroupRequest: BalanceGroupRequest) =
        BalanceGroup(
            id = null,
            groupName = balanceGroupRequest.groupName,
            groupMemberIds = balanceGroupRequest.groupMemberIds,
            expenseIds = emptyList(),
            groupOwnerUserId = securityPort.getCurrentLoginUserId(),
            createdAt = OffsetDateTime.now(),
            updatedAt = null
        )

    private fun mapExpense(expenseRequest: ExpenseRequest, balanceGroupId: UUID) = Expense(
        id = null,
        name = expenseRequest.name,
        balanceGroupId = balanceGroupId,
        expenseOwnerId = securityPort.getCurrentLoginUserId(),
        amount = expenseRequest.amount,
        splitType = expenseRequest.splitType,
        createdAt = OffsetDateTime.now(),
        updatedAt = null
    )

    companion object {
        private const val IS_GROUP_MEMBER_OR_ADMIN_MATCHER =
            "@springSecurityAdapter.isAdmin() || @springSecurityAdapter.isBalanceGroupMember(#balanceGroupId)"

        private const val IS_OWNER_OR_ADMIN_MATCHER =
            "@springSecurityAdapter.isAdmin() || @springSecurityAdapter.isBalanceGroupCreator(#balanceGroupId)"

        private const val IS_EXPENSE_OWNER_OR_ADMIN_MATCHER =
            "@springSecurityAdapter.isAdmin() || @springSecurityAdapter.isExpenseCreator(#expenseId)"
        private val LOGGER = KotlinLogging.logger {}
    }
}

@ControllerAdvice(assignableTypes = [BalanceGroupController::class])
class BalanceGroupExceptionHandler {

    @ExceptionHandler(BalanceGroupNotFoundException::class)
    fun handleBalanceGroupNotFoundException(ex: BalanceGroupNotFoundException): ResponseEntity<Void> {
        LOGGER.warn { "Balance group not found exception: ${ex.message}" }
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(ExpenseNotFoundException::class)
    fun handleExpenseNotFoundException(ex: ExpenseNotFoundException): ResponseEntity<Void> {
        LOGGER.warn { "Expense not found exception: ${ex.message}" }
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(ExpenseValidationException::class)
    fun handleExpenseValidationException(ex: ExpenseValidationException): ResponseEntity<ProblemDetail> {
        LOGGER.warn { "Expense validation error: ${ex.message}" }
        val error = ProblemDetail
            .forStatusAndDetail(HttpStatusCode.valueOf(400), ex.message)
        return ResponseEntity.badRequest()
            .body(error)
    }

    @ExceptionHandler(BalanceGroupValidationException::class)
    fun handleBalanceGroupValidationException(ex: BalanceGroupValidationException): ResponseEntity<ProblemDetail> {
        LOGGER.warn { "Balance group validation error: ${ex.message}" }
        val error = ProblemDetail
            .forStatusAndDetail(HttpStatusCode.valueOf(400), ex.message)
        return ResponseEntity.badRequest()
            .body(error)
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}
