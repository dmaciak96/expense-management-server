package com.example.expense_management_server.adapter.security

import com.example.expense_management_server.domain.facade.IUserManagementFacade
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class DatabaseUserDetailsService(
    private val userManagementFacade: IUserManagementFacade
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val userDomainModel = userManagementFacade.getUserByEmail(email)
        return User.withUsername(userDomainModel.email)
            .password(userDomainModel.passwordHash)
            .roles(userDomainModel.role.name)
            .build()
    }
}