package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class MongoUserDetailsService(
    private val userPersistencePort: UserPersistencePort
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userPersistencePort.findByEmail(email)
        return User.builder()
            .username(user.email)
            .password(user.password)
            .authorities(listOf(SimpleGrantedAuthority("ROLE_USER")))
            .build()
    }
}