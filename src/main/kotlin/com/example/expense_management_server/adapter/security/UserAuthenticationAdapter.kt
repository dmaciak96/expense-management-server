package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.model.UserAccount
import com.example.expense_management_server.domain.user.port.UserAuthenticationPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

@Component
class UserAuthenticationAdapter(
    private val clock: Clock,
    private val jwtEncoder: JwtEncoder,
    private val authenticationManager: AuthenticationManager,
    @Value("\${spring.security.jwt.issuer}") val issuer: String,
) : UserAuthenticationPort {
    override fun authenticateAndGenerateJwtToken(email: String, password: String): String {
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        val userAccount = authentication.principal as UserAccount
        val now = Instant.now(clock)
        val expiresAtOneHour = now.plusSeconds(3600)

        val claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(expiresAtOneHour)
            .subject(userAccount.id.toString())
            .claim("roles", listOf(userAccount.role.name))
            .build()

        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }
}