package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.config.JwtProperties
import com.example.expense_management_server.domain.authentication.model.AuthenticationToken
import com.example.expense_management_server.domain.authentication.model.UserAuthentication
import com.example.expense_management_server.domain.authentication.port.AuthenticationPort
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JwtAuthenticationAdapter(
    private val authenticationManager: AuthenticationManager,
    private val jwtProperties: JwtProperties,
    private val jwtEncoder: JwtEncoder,
) : AuthenticationPort {

    override fun generateAuthenticationToken(userAuthentication: UserAuthentication): AuthenticationToken {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                userAuthentication.email,
                userAuthentication.password
            )
        )
        val token = generateJwtToken(authentication)
        return AuthenticationToken(token)
    }

    private fun generateJwtToken(authentication: Authentication): String {
        val now = Instant.now()

        val roles = authentication.authorities
            .map { it.authority }

        val claims = JwtClaimsSet.builder()
            .issuer(ISSUER)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtProperties.expiration))
            .subject(authentication.name)
            .claim(ROLES_CLAIM_KEY, roles)
            .build()

        val header = JwsHeader
            .with(MacAlgorithm.HS256)
            .type(TOKEN_HEADER_TYPE)
            .build()

        return jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).tokenValue
    }

    companion object {
        private const val ISSUER = "expense-management"
        private const val ROLES_CLAIM_KEY = "roles"
        private const val TOKEN_HEADER_TYPE = "JWT"
    }
}