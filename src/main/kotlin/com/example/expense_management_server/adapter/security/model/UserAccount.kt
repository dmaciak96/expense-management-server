package com.example.expense_management_server.adapter.security.model

import com.example.expense_management_server.domain.user.model.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class UserAccount(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val role: UserRole
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_" + role.name))

    override fun getPassword(): String = passwordHash

    override fun getUsername(): String = email
}