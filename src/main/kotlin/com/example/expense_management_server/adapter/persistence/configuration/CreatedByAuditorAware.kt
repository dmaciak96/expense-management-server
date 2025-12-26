package com.example.expense_management_server.adapter.persistence.configuration

import com.example.expense_management_server.domain.user.port.ISecurityPort
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
@EnableJpaAuditing
class CreatedByAuditorAware(
    private val securityPort: ISecurityPort,
) : AuditorAware<UUID> {
    override fun getCurrentAuditor(): Optional<UUID> {
        return Optional.of(securityPort.getCurrentLoginUserId())
    }
}
