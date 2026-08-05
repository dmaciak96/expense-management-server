package com.example.expense_management_server.application.account_members

import com.example.expense_management_server.TestConstants
import com.example.expense_management_server.application.account.FetchAccountByIdUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchAllMembersFromAccountUseCaseTest {

    @Mock
    private lateinit var fetchAccountByIdUseCase: FetchAccountByIdUseCase

    private lateinit var useCase: FetchAllMembersFromAccountUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        useCase = FetchAllMembersFromAccountUseCase(
            fetchAccountByIdUseCase = fetchAccountByIdUseCase
        )
    }

    @Test
    fun `should return all account members`() {
        // given
        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(TestConstants.ACCOUNT)

        // when
        val result = useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        assertEquals(TestConstants.ACCOUNT.members.toList(), result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
    }

    @Test
    fun `should return empty list when account has no members`() {
        // given
        val account = TestConstants.ACCOUNT.copy(members = emptySet())

        whenever(fetchAccountByIdUseCase.execute(TestConstants.ACCOUNT_ID))
            .thenReturn(account)

        // when
        val result = useCase.execute(TestConstants.ACCOUNT_ID)

        // then
        assertEquals(emptyList<Any>(), result)

        verify(fetchAccountByIdUseCase).execute(TestConstants.ACCOUNT_ID)
    }
}
