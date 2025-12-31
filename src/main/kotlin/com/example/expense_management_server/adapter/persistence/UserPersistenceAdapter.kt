package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.UserEntity
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.user.exception.UserNotFoundException
import com.example.expense_management_server.domain.user.model.UserModel
import com.example.expense_management_server.domain.user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository
) : UserPersistencePort {

    override fun saveOrUpdateUserAccount(userModel: UserModel): UserModel =
        if (userModel.id == null) {
            saveUserAccount(userModel)
        } else {
            updateUserAccount(userModel)
        }

    private fun saveUserAccount(userModel: UserModel): UserModel {
        LOGGER.debug { "Saving user account in database" }
        val savedUser = userRepository.save(mapToEntity(userModel))
        LOGGER.debug { "User account saved in database" }
        return mapToModel(savedUser)
    }

    private fun updateUserAccount(userModel: UserModel): UserModel {
        LOGGER.debug { "Updating user account ${userModel.id}" }
        val version = userRepository.findById(userModel.id!!).map { it.version }
            .orElseThrow { UserNotFoundException() }
        val savedUser = userRepository.saveAndFlush(mapToEntity(userModel, version))
        LOGGER.debug { "User account was updated" }
        return mapToModel(savedUser)
    }

    override fun findUserAccountByEmail(email: String): UserModel? {
        LOGGER.debug { "Get user account by e-mail address $email" }
        val user = userRepository.findByEmail(email) ?: return null
        return mapToModel(user)
    }

    override fun findUserAccountById(id: UUID): UserModel? {
        LOGGER.debug { "Get user account by id" }
        return userRepository.findById(id)
            .map { mapToModel(it) }
            .getOrNull()
    }

    override fun deleteUser(userModel: UserModel) {
        userRepository.delete(mapToEntity(userModel))
        LOGGER.debug { "User ${userModel.email} was removed from database" }
    }

    private fun mapToEntity(userModel: UserModel, version: Int? = null): UserEntity =
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

    private fun mapToModel(userEntity: UserEntity): UserModel =
        UserModel(
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