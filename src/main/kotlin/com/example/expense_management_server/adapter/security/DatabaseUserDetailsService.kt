package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.adapter.security.model.UserAccount
import com.example.expense_management_server.domain.service.UserManagementService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class DatabaseUserDetailsService(
    private val userManagementService: UserManagementService
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val userDomainModel = userManagementService.getUserByEmail(email)
        return UserAccount(
            id = userDomainModel.id!!,
            email = userDomainModel.email,
            passwordHash = userDomainModel.passwordHash,
            role = userDomainModel.role
        )
    }
}