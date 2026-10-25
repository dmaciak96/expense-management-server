package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.config.JwtProperties
import com.example.expense_management_server.domain.application_user.exception.VerificationTokenException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.authentication.model.AuthenticationToken
import com.example.expense_management_server.domain.authentication.model.UserAuthentication
import com.example.expense_management_server.domain.authentication.port.AuthenticationPort
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Component
import java.time.Instant


@Component
class JwtAuthenticationAdapter(
    private val authenticationManager: AuthenticationManager,
    private val jwtProperties: JwtProperties,
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder,
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

    override fun generateAccountVerificationToken(applicationUser: ApplicationUser): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .subject(applicationUser.id.toString())
            .claim(EMAIL_CLAIM_KEY, applicationUser.email)
            .claim(PURPOSE_CLAIM_KEY, PURPOSE_CLAIM_VALUE)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(900))
            .build()

        val header = JwsHeader
            .with(MacAlgorithm.HS256)
            .type(TOKEN_HEADER_TYPE)
            .build()

        return jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).tokenValue
    }

    override fun validateVerificationTokenAndGetEmail(token: String): String {
        try {
            val jwt = jwtDecoder.decode(token)
            val email = jwt.getClaimAsString(EMAIL_CLAIM_KEY)
                ?: throw VerificationTokenException(message = "Missing email claim")
            val purpose = jwt.getClaimAsString(PURPOSE_CLAIM_KEY)
                ?: throw VerificationTokenException(message = "Missing purpose claim")
            if (purpose != PURPOSE_CLAIM_VALUE) {
                throw VerificationTokenException(message = "Invalid token")
            }
            return email
        } catch (e: JwtValidationException) {
            throw VerificationTokenException(cause = e)
        } catch (e: JwtException) {
            throw VerificationTokenException(cause = e)
        }
    }

    companion object {
        private const val ISSUER = "expense-management"
        private const val ROLES_CLAIM_KEY = "roles"
        private const val TOKEN_HEADER_TYPE = "JWT"
        private const val EMAIL_CLAIM_KEY = "email"
        private const val PURPOSE_CLAIM_KEY = "purpose"
        private const val PURPOSE_CLAIM_VALUE = "email_verification"
    }
}