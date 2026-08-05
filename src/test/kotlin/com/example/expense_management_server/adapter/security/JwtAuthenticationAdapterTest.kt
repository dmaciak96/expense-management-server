package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.config.JwtProperties
import com.example.expense_management_server.domain.authentication.model.UserAuthentication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters

class JwtAuthenticationAdapterTest {

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @Mock
    private lateinit var jwtEncoder: JwtEncoder

    @Mock
    private lateinit var authentication: Authentication

    @Mock
    private lateinit var jwt: Jwt

    private lateinit var adapter: JwtAuthenticationAdapter

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        adapter = JwtAuthenticationAdapter(
            authenticationManager = authenticationManager,
            jwtProperties = JwtProperties(
                secret = "test-secret",
                expiration = 3600
            ),
            jwtEncoder = jwtEncoder
        )
    }

    @Test
    fun `should authenticate user and generate jwt token`() {
        // given
        val userAuthentication = UserAuthentication(
            email = "jon.snow@night-watch.com",
            password = "Password1!"
        )

        whenever(authenticationManager.authenticate(any()))
            .thenReturn(authentication)

        whenever(authentication.name)
            .thenReturn(userAuthentication.email)

        whenever(authentication.authorities)
            .thenReturn(listOf(SimpleGrantedAuthority("ROLE_USER")))

        whenever(jwtEncoder.encode(any()))
            .thenReturn(jwt)

        whenever(jwt.tokenValue)
            .thenReturn("jwt-token")

        // when
        val result = adapter.generateAuthenticationToken(userAuthentication)

        // then
        assertEquals("jwt-token", result.token)

        verify(authenticationManager).authenticate(any())

        val captor = ArgumentCaptor.forClass(JwtEncoderParameters::class.java)
        verify(jwtEncoder).encode(capture(captor))

        val parameters = captor.value
        assertEquals("expense-management", parameters.claims.issuer)
        assertEquals(userAuthentication.email, parameters.claims.subject)
        assertEquals(listOf("ROLE_USER"), parameters.claims.getClaim<List<String>>("roles"))
        assertEquals("JWT", parameters.jwsHeader?.type)
    }
}
