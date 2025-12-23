package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.UserEntity
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.user.port.IUserPersistencePort
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.UserDomainModel
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository
) : IUserPersistencePort {

    override fun saveOrUpdateUserAccount(userModel: UserDomainModel): UserDomainModel =
        if (userModel.id == null) {
            saveUserAccount(userModel)
        } else {
            updateUserAccount(userModel)
        }

    private fun saveUserAccount(userModel: UserDomainModel): UserDomainModel {
        LOGGER.debug { "Saving user account in database" }
        val savedUser = userRepository.save(map(userModel))
        LOGGER.debug { "User account saved in database" }
        return map(savedUser)
    }

    private fun updateUserAccount(userModel: UserDomainModel): UserDomainModel {
        LOGGER.debug { "Updating user account ${userModel.id}" }
        val version = userRepository.findById(userModel.id!!).map { it.version }
            .orElseThrow { UserNotFoundException() }
        val savedUser = userRepository.saveAndFlush(map(userModel, version))
        LOGGER.debug { "User account was updated" }
        return map(savedUser)
    }

    override fun findUserAccountByEmail(email: String): UserDomainModel? {
        LOGGER.debug { "Get user account by e-mail address $email" }
        val user = userRepository.findByEmail(email) ?: return null
        return map(user)
    }

    override fun findUserAccountById(id: UUID): UserDomainModel? {
        LOGGER.debug { "Get user account by id" }
        return userRepository.findById(id)
            .map { map(it) }
            .getOrNull()
    }

    override fun deleteUser(userDomainModel: UserDomainModel) {
        userRepository.delete(map(userDomainModel))
        LOGGER.debug { "User ${userDomainModel.email} was removed from database" }
    }

    private fun map(userModel: UserDomainModel, version: Int? = null): UserEntity =
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
            accountStatus = userModel.accountStatus,
            version = version
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