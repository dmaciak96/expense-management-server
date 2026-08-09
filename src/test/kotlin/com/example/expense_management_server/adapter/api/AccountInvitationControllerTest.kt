package com.example.expense_management_server.adapter.api

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.adapter.api.account_invitation.AccountInvitationController
import com.example.expense_management_server.adapter.api.account_invitation.AccountInvitationControllerAdvice
import com.example.expense_management_server.application.account_invitation.AcceptAccountInvitationUseCase
import com.example.expense_management_server.application.account_invitation.CreateAccountInvitationUseCase
import com.example.expense_management_server.application.account_invitation.FetchAccountInvitationsByAccountIdUseCase
import com.example.expense_management_server.application.account_invitation.UpdateAccountInvitationStatusUseCase
import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account_invitation.exception.InvitationExpiredException
import com.example.expense_management_server.domain.account_invitation.exception.InvitationNotFoundException
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitationStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AccountInvitationControllerTest {
    @Mock
    lateinit var acceptAccountInvitationUseCase: AcceptAccountInvitationUseCase

    @Mock
    lateinit var createAccountInvitationUseCase: CreateAccountInvitationUseCase

    @Mock
    lateinit var fetchAccountInvitationsByAccountIdUseCase: FetchAccountInvitationsByAccountIdUseCase

    @Mock
    lateinit var updateAccountInvitationStatusUseCase: UpdateAccountInvitationStatusUseCase

    @Mock
    lateinit var fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val controller = AccountInvitationController(
            acceptAccountInvitationUseCase,
            createAccountInvitationUseCase,
            fetchAccountInvitationsByAccountIdUseCase,
            updateAccountInvitationStatusUseCase,
            fetchCurrentLoginUserUseCase
        )
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(AccountInvitationControllerAdvice())
            .build()
    }

    @Test
    fun `should create account invitation`() {
        // given
        whenever(fetchCurrentLoginUserUseCase.execute()).thenReturn(TestConstants.APPLICATION_USER_ONE)
        whenever(createAccountInvitationUseCase.execute(any())).thenReturn(TestConstants.INVITATION)
        val body =
            """{"accountId":"${TestConstants.ACCOUNT_ID}","email":"${TestConstants.USER_TWO_EMAIL}","status":"PENDING"}"""

        // when & then
        mockMvc.post("/account-invitations") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(TestConstants.INVITATION_ID.toString()) }
            jsonPath("$.email") { value(TestConstants.USER_TWO_EMAIL) }
            jsonPath("$.status") { value("PENDING") }
        }

        verify(createAccountInvitationUseCase).execute(any())
    }

    @Test
    fun `should return invitations by account id`() {
        // given
        whenever(fetchAccountInvitationsByAccountIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(listOf(TestConstants.INVITATION))

        // when & then
        mockMvc.get("/account-invitations") {
            param("accountId", TestConstants.ACCOUNT_ID.toString())
        }.andExpect {
            status { isOk() }
            jsonPath("$[0].id") { value(TestConstants.INVITATION_ID.toString()) }
        }
    }

    @Test
    fun `should accept account invitation`() {
        // given
        whenever(acceptAccountInvitationUseCase.execute(TestConstants.INVITATION_ID)).thenReturn(TestConstants.ACCOUNT)

        // when & then
        mockMvc.post("/account-invitations/${TestConstants.INVITATION_ID}")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(TestConstants.ACCOUNT_ID.toString()) }
            }
    }

    @Test
    fun `should update account invitation status`() {
        // given
        val updated = TestConstants.INVITATION.copy(status = AccountMemberInvitationStatus.REJECTED)
        whenever(
            updateAccountInvitationStatusUseCase.execute(
                eq(TestConstants.INVITATION_ID),
                eq(AccountMemberInvitationStatus.REJECTED)
            )
        )
            .thenReturn(updated)
        val body = """{"status":"REJECTED"}"""

        // when & then
        mockMvc.put("/account-invitations/${TestConstants.INVITATION_ID}") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }.andExpect {
            status { isOk() }
            jsonPath("$.status") { value("REJECTED") }
        }
    }

    @Test
    fun `should return not found when invitation does not exist`() {
        // given
        whenever(acceptAccountInvitationUseCase.execute(TestConstants.INVITATION_ID))
            .thenThrow(InvitationNotFoundException("Invitation not found"))

        // when & then
        mockMvc.post("/account-invitations/${TestConstants.INVITATION_ID}")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.message") { value("Invitation not found") }
                jsonPath("$.status") { value(404) }
            }
    }

    @Test
    fun `should return forbidden when invitation is expired`() {
        // given
        whenever(acceptAccountInvitationUseCase.execute(TestConstants.INVITATION_ID))
            .thenThrow(InvitationExpiredException("Invitation expired"))

        // when & then
        mockMvc.post("/account-invitations/${TestConstants.INVITATION_ID}")
            .andExpect {
                status { isForbidden() }
                jsonPath("$.status") { value(403) }
            }
    }

    @Test
    fun `should return not found when account does not exist`() {
        // given
        whenever(fetchAccountInvitationsByAccountIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenThrow(AccountNotFoundException("Account not found"))

        // when & then
        mockMvc.get("/account-invitations") {
            param("accountId", TestConstants.ACCOUNT_ID.toString())
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.status") { value(404) }
        }
    }
}
