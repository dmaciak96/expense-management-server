package com.example.expense_management_server.application.application_user

import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class RemoveCurrentLoginUserUseCase(
    private val fetchCurrentLoginUserUseCase: FetchCurrentLoginUserUseCase,
    private val userPersistencePort: UserPersistencePort
) {
    fun execute() {
        LOGGER.info { "Removing current login user" }
        val currentLoginUser = fetchCurrentLoginUserUseCase.execute()
        userPersistencePort.deleteById(currentLoginUser.id)
        LOGGER.debug { "User ${currentLoginUser.email} was removed" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}