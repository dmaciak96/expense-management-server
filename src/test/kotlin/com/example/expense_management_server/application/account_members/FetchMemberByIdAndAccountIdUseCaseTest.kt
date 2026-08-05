package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import com.example.expense_management_server.domain.account.exception.AccountMemberNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

class FetchMemberByIdAndAccountIdUseCaseTest {

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    private lateinit var useCase: FetchMemberByIdAndAccountIdUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = FetchMemberByIdAndAccountIdUseCase(
            fetchAccountByIdUseCase = fetchAccountByIdUseCase
        )
    }

    @Test
    fun `should return account member by user id`() {
        // given
        val expectedMember = TestConstants.APPLICATION_USER_TWO.toAccountMember()

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        val result = useCase.execute(
            accountId = TestConstants.ACCOUNT_ID,
            applicationUserId = TestConstants.USER_TWO_ID
        )

        // then
        assertEquals(expectedMember, result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should throw AccountMemberNotFoundException when member does not exist`() {
        // given
        val applicationUserId = UUID.randomUUID()

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        assertThrows<AccountMemberNotFoundException> {
            useCase.execute(
                accountId = TestConstants.ACCOUNT_ID,
                applicationUserId = applicationUserId
            )
        }

        // then
        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
    }
}
