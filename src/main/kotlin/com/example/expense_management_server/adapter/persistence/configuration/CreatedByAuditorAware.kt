package com.example.expense_management_server.adapter.persistence.configuration

import com.example.expense_management_server.adapter.persistence.model.UserEntity
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.user.port.ISecurityPort
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class CreatedByAuditorAware(
    private val securityPort: ISecurityPort,
    private val userRepository: UserRepository
) : AuditorAware<UserEntity> {
    override fun getCurrentAuditor(): Optional<UserEntity> {
        val userDomainModel = securityPort.getCurrentLoginUser()
        return userRepository.findById(userDomainModel.id!!)
    }
}