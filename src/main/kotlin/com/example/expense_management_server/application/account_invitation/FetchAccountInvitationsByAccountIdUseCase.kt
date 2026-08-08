package com.example.expense_management_server.application.account_invitation

import com.example.expense_management_server.application.application_user.FetchCurrentLoginUserUseCase
import com.example.expense_management_server.domain.account.exception.AccountNotFoundException
import com.example.expense_management_server.domain.account.port.AccountPersistencePort
import com.example.expense_management_server.domain.account_invitation.model.AccountMemberInvitation
import com.example.expense_management_server.domain.account_invitation.port.InvitationPersistencePort
import org.springframework.stereotype.Component
import java.util.*

@Component
class FetchAccountInvitationsByAccountIdUseCase(
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val invitationPersistencePort: InvitationPersistencePort,
    private val accountPersistencePort: AccountPersistencePort
) {

    fun execute(accountId: UUID): List<AccountMemberInvitation> {
        val currentUser = fetchCurrentLoginUserUseCase.execute()
        val account = accountPersistencePort.findById(accountId)
        if (account.createdBy.id != currentUser.id) {
            throw AccountNotFoundException("Account with id $accountId does not exist")
        }
        return invitationPersistencePort.findAllByAccountId(accountId)
    }
}