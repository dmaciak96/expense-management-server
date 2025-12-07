package com.example.expense_management_server.unit.adapter.security

import com.example.expense_management_server.adapter.security.PasswordEncoderAdapter
import com.example.expense_management_server.domain.user.exception.PasswordEncodingException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class PasswordEncoderAdapterTest {

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `should return encoded password when Spring encoder returns non null value`() {
        // given
        val adapter = PasswordEncoderAdapter(passwordEncoder)
        whenever(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD)

        // when
        val result = adapter.encodePassword(RAW_PASSWORD)

        // then
        assertThat(result).isEqualTo(ENCODED_PASSWORD)
        verify(passwordEncoder).encode(RAW_PASSWORD)
        verifyNoMoreInteractions(passwordEncoder)
    }

    @Test
    fun `should throw PasswordEncodingException when Spring encoder returns null`() {
        // given
        val adapter = PasswordEncoderAdapter(passwordEncoder)
        whenever(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(null)

        // when & then
        assertThrows<PasswordEncodingException> {
            adapter.encodePassword(RAW_PASSWORD)
        }

        verify(passwordEncoder).encode(RAW_PASSWORD)
        verifyNoMoreInteractions(passwordEncoder)
    }

    companion object {
        private const val RAW_PASSWORD = "StrongPassword123!"
        private const val ENCODED_PASSWORD = "encoded-strong-password"
    }
}