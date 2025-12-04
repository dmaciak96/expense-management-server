package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.domain.port.IUserPersistencePort
import com.example.expense_management_server.domain.user.model.UserDomainModel
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository
) : IUserPersistencePort {

    override fun saveUserAccount(userModel: UserDomainModel): UserDomainModel {
        LOGGER.info { "Saving user account in database" }
        val savedUser = userRepository.save(map(userModel))
        LOGGER.info { "User account saved in database" }
        return map(savedUser)
    }

    override fun findUserAccountByEmail(email: String): UserDomainModel? {
        LOGGER.info { "Get user account by e-mail address $email" }
        val user = userRepository.findByEmail(email) ?: return null
        return map(user)
    }

    override fun findUserAccountById(id: UUID): UserDomainModel? {
        LOGGER.info { "Get user account by id" }
        return userRepository.findById(id)
            .map { map(it) }
            .getOrNull()
    }

    private fun map(userModel: UserDomainModel): UserEntity =
        UserEntity(
            id = userModel.id,
            email = userModel.email,
            nickname = userModel.nickname,
            passwordHash = userModel.passwordHash,
            role = userModel.role,
            isEmailVerified = userModel.isEmailVerified,
            createdAt = userModel.createdAt,
            updatedAt = userModel.updatedAt,
            lastLoginAt = userModel.lastLoginAt,
            accountStatus = userModel.accountStatus
        )

    private fun map(userEntity: UserEntity): UserDomainModel =
        UserDomainModel(
            id = userEntity.id,
            email = userEntity.email,
            nickname = userEntity.nickname,
            passwordHash = userEntity.passwordHash,
            role = userEntity.role,
            isEmailVerified = userEntity.isEmailVerified,
            createdAt = userEntity.createdAt,
            updatedAt = userEntity.updatedAt,
            lastLoginAt = userEntity.lastLoginAt,
            accountStatus = userEntity.accountStatus
        )

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}