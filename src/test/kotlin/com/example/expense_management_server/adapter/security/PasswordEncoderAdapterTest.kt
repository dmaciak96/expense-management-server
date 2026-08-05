package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.domain.application_user.exception.PasswordEncodingException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

class PasswordEncoderAdapterTest {

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var adapter: PasswordEncoderAdapter

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        adapter = PasswordEncoderAdapter(
            passwordEncoder = passwordEncoder
        )
    }

    @Test
    fun `should encode password`() {
        // given
        val rawPassword = "Password1!"
        val encodedPassword = "encoded-password"

        whenever(passwordEncoder.encode(rawPassword))
            .thenReturn(encodedPassword)

        // when
        val result = adapter.encode(rawPassword)

        // then
        assertEquals(encodedPassword, result)

        verify(passwordEncoder).encode(rawPassword)
    }

    @Test
    fun `should throw PasswordEncodingException when encoder returns null`() {
        // given
        val rawPassword = "Password1!"

        whenever(passwordEncoder.encode(rawPassword))
            .thenReturn(null)

        // when & then
        assertThrows<PasswordEncodingException> {
            adapter.encode(rawPassword)
        }

        verify(passwordEncoder).encode(rawPassword)
    }
}
