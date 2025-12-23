package com.example.expense_management_server.unit.domain.balancegroup

import com.example.expense_management_server.domain.balancegroup.BalanceGroupManagementService
import com.example.expense_management_server.domain.balancegroup.BalanceGroupValidator
import com.example.expense_management_server.domain.balancegroup.model.BalanceGroupDomainModel
import com.example.expense_management_server.domain.balancegroup.port.IBalanceGroupPersistencePort
import com.example.expense_management_server.domain.user.model.UserDomainModel
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class BalanceGroupManagementServiceTest {

    @Mock
    private lateinit var balanceGroupPersistencePort: IBalanceGroupPersistencePort

    @Mock
    private lateinit var balanceGroupValidator: BalanceGroupValidator

    @Mock
    private lateinit var userPersistencePort: IUserPersistencePort

    private lateinit var balanceGroupService: BalanceGroupManagementService

    @BeforeEach
    fun setUp() {
        balanceGroupService =
            BalanceGroupManagementService(
                balanceGroupPersistencePort = balanceGroupPersistencePort,
                balanceGroupValidator = balanceGroupValidator,
                userPersistencePort = userPersistencePort
            )
    }

    @Test
    fun `when data is valid then save balance group`() {
        // given
        whenever(balanceGroupPersistencePort.save(BALANCE_GROUP))
            .thenReturn(BALANCE_GROUP)

        // when
        val result = balanceGroupService.save(BALANCE_GROUP)

        // then
        assertThat(result).isEqualTo(BALANCE_GROUP)
        verify(balanceGroupValidator).validate(BALANCE_GROUP)
        verify(balanceGroupPersistencePort).save(BALANCE_GROUP)
        verifyNoInteractions(userPersistencePort)
    }

    @Test
    fun `when data is valid then update balance group`() {
        // given
        val expected = BALANCE_GROUP.copy(groupName = "updated")
        whenever(balanceGroupPersistencePort.update(BALANCE_GROUP_ID, expected))
            .thenReturn(expected)
        // when
        val result = balanceGroupService.update(BALANCE_GROUP_ID, expected)

        // then
        assertThat(result).isEqualTo(expected)
        verify(balanceGroupValidator).validateForUpdate(BALANCE_GROUP_ID, expected)
        verify(balanceGroupPersistencePort).update(BALANCE_GROUP_ID, expected)
        verifyNoInteractions(userPersistencePort)
    }

    @Test
    fun `when balance group exists then return balance group by id`() {
        // given
        whenever(balanceGroupValidator.getIfBalanceGroupExists(BALANCE_GROUP_ID))
            .thenReturn(BALANCE_GROUP)

        // when
        val result = balanceGroupService.getById(BALANCE_GROUP_ID)

        // then
        assertThat(result).isEqualTo(BALANCE_GROUP)
        verify(balanceGroupValidator).getIfBalanceGroupExists(BALANCE_GROUP_ID)
        verifyNoInteractions(userPersistencePort, balanceGroupPersistencePort)
    }

    @Test
    fun `when user is group member should return all balance groups`() {
        // given
        val user = mock<UserDomainModel>()
        whenever(userPersistencePort.findUserAccountById(MEMBER_ID))
            .thenReturn(user)
        whenever(balanceGroupPersistencePort.getAllWhereUserIsGroupMember(MEMBER_ID))
            .thenReturn(listOf(BALANCE_GROUP))

        // when
        val result = balanceGroupService.getAllWhereUserIsGroupMember(MEMBER_ID)

        // then
        assertThat(result).isEqualTo(listOf(BALANCE_GROUP))
        verify(balanceGroupPersistencePort).getAllWhereUserIsGroupMember(MEMBER_ID)
        verify(userPersistencePort).findUserAccountById(MEMBER_ID)
        verifyNoInteractions(balanceGroupValidator)
    }

    @Test
    fun `when balance group exists then delete balance group`() {
        // given
        whenever(balanceGroupValidator.getIfBalanceGroupExists(BALANCE_GROUP_ID))
            .thenReturn(BALANCE_GROUP)

        // when
        balanceGroupService.delete(BALANCE_GROUP_ID)

        // then
        verify(balanceGroupPersistencePort).delete(BALANCE_GROUP_ID)
        verify(balanceGroupValidator).getIfBalanceGroupExists(BALANCE_GROUP_ID)
        verifyNoInteractions(userPersistencePort)
    }

    companion object {
        private val BALANCE_GROUP_ID = UUID.fromString("72f979a9-a930-4bcb-8996-313ad44a7772")
        private val GROUP_OWNER_ID = UUID.fromString("a40f053f-840d-483e-b329-8287dd89a660")
        private val EXPENSE_ID = UUID.fromString("7f8e2631-d780-405c-85dd-2638f95e3134")
        private val MEMBER_ID = UUID.fromString("51372e8b-83b0-4525-9fe0-c48fea02f39d")
        private const val GROUP_NAME = "test"

        private val BALANCE_GROUP = BalanceGroupDomainModel(
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