package com.example.expense_management_server.adapter.security.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtConfiguration(
    private val jwtProperties: JwtProperties
) {
    @Bean
    fun jwtSecretKey(): SecretKey {
        val decodedSecret = Base64.getDecoder().decode(jwtProperties.secret)

        require(decodedSecret.size >= 32) {
            "JWT secret must contain at least 32 bytes"
        }

        return SecretKeySpec(decodedSecret, "HmacSHA256")
    }

    @Bean
    fun jwtEncoder(secretKey: SecretKey): JwtEncoder {
        val secret = ImmutableSecret<SecurityContext>(secretKey)

        return NimbusJwtEncoder(secret)
    }

    @Bean
    fun jwtDecoder(secretKey: SecretKey): JwtDecoder =
        NimbusJwtDecoder
            .withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
}