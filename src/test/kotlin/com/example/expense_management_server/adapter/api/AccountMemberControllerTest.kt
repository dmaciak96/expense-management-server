package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.api.account_members.AccountMemberController
import com.example.expense_management_server.adapter.api.account_members.AccountMemberControllerAdvice
import com.example.expense_management_server.application.account_members.AddMemberToAccountUseCase
import com.example.expense_management_server.application.account_members.DeleteMemberFromAccountUseCase
import com.example.expense_management_server.application.account_members.FetchAllMembersFromAccountUseCase
import com.example.expense_management_server.application.account_members.FetchMemberByIdAndAccountIdUseCase
import com.example.expense_management_server.domain.account.exception.AccountMemberNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AccountMemberControllerTest {

    @Mock
    private lateinit var addMemberToAccountUseCase: AddMemberToAccountUseCase

    @Mock
    private lateinit var deleteMemberFromAccountUseCase: DeleteMemberFromAccountUseCase

    @Mock
    private lateinit var fetchAllMembersFromAccountUseCase: FetchAllMembersFromAccountUseCase

    @Mock
    private lateinit var fetchMemberByIdAndAccountIdUseCase: FetchMemberByIdAndAccountIdUseCase

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        val controller = AccountMemberController(
            addMemberToAccountUseCase,
            deleteMemberFromAccountUseCase,
            fetchAllMembersFromAccountUseCase,
            fetchMemberByIdAndAccountIdUseCase
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(AccountMemberControllerAdvice())
            .build()
    }

    @Test
    fun `should add member to account`() {
        // given
        whenever(
            addMemberToAccountUseCase.execute(
                TestConstants.ACCOUNT_ID,
                TestConstants.USER_TWO_ID
            )
        ).thenReturn(TestConstants.ACCOUNT)

        // when & then
        mockMvc.post(
            "/accounts/${TestConstants.ACCOUNT_ID}/members/${TestConstants.USER_TWO_ID}"
        ).andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
        }

        verify(addMemberToAccountUseCase)
            .execute(TestConstants.ACCOUNT_ID, TestConstants.USER_TWO_ID)
    }

    @Test
    fun `should delete member from account`() {
        // given
        whenever(
            deleteMemberFromAccountUseCase.execute(
                TestConstants.ACCOUNT_ID,
                TestConstants.USER_TWO_ID
            )
        ).thenReturn(TestConstants.ACCOUNT)

        // when & then
        mockMvc.delete(
            "/accounts/${TestConstants.ACCOUNT_ID}/members/${TestConstants.USER_TWO_ID}"
        ).andExpect {
            status { isOk() }
            jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
        }

        verify(deleteMemberFromAccountUseCase)
            .execute(TestConstants.ACCOUNT_ID, TestConstants.USER_TWO_ID)
    }

    @Test
    fun `should return all account members`() {
        // given
        whenever(fetchAllMembersFromAccountUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT.members.toList())

        // when & then
        mockMvc.get("/accounts/${TestConstants.ACCOUNT_ID}/members")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
            }

        verify(fetchAllMembersFromAccountUseCase).execute(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should return member by id`() {
        // given
        val member = TestConstants.APPLICATION_USER_TWO.toAccountMember()

        whenever(
            fetchMemberByIdAndAccountIdUseCase.execute(
                TestConstants.ACCOUNT_ID,
                TestConstants.USER_TWO_ID
            )
        ).thenReturn(member)

        // when & then
        mockMvc.get(
            "/accounts/${TestConstants.ACCOUNT_ID}/members/${TestConstants.USER_TWO_ID}"
        ).andExpect {
            status { isOk() }
            jsonPath("$.applicationUserId") { value(TestConstants.USER_TWO_ID.toString()) }
        }
    }

    @Test
    fun `should return not found error response when member does not exist`() {
        // given
        whenever(
            fetchMemberByIdAndAccountIdUseCase.execute(
                TestConstants.ACCOUNT_ID,
                TestConstants.USER_TWO_ID
            )
        ).thenThrow(AccountMemberNotFoundException("Account member not found"))

        // when & then
        mockMvc.get(
            "/accounts/${TestConstants.ACCOUNT_ID}/members/${TestConstants.USER_TWO_ID}"
        ).andExpect {
            status { isNotFound() }
            jsonPath("$.message") { value("Account member not found") }
            jsonPath("$.status") { value(404) }
        }
    }
}
