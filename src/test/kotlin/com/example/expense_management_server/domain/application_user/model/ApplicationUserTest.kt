package com.example.expense_management_server.domain.application_user.model

import com.example.expense_management_server.TestConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplicationUserTest {

    @Test
    fun `should map application user to account member`() {
        // given
        val user = TestConstants.APPLICATION_USER_ONE

        // when
        val result = user.toAccountMember()

        // then
        assertEquals(user.id, result.applicationUserId)
        assertEquals(user.firstName, result.firstName)
        assertEquals(user.lastName, result.lastName)
        assertEquals(user.displayName, result.displayName)
        assertEquals(user.avatarUrl, result.avatarUrl)
    }
}
