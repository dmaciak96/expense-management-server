package com.example.expense_management_server.adapter.persistence

import com.example.expense_management_server.adapter.persistence.model.ApplicationUserEntity
import com.example.expense_management_server.adapter.persistence.repository.UserRepository
import com.example.expense_management_server.domain.application_user.exception.UserNotFoundException
import com.example.expense_management_server.domain.application_user.model.ApplicationUser
import com.example.expense_management_server.domain.application_user.port.UserPersistencePort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository
) : UserPersistencePort {
    override fun create(user: ApplicationUser): ApplicationUser {
        LOGGER.debug { "Saving new user in DB $user" }
        val savedEntity = userRepository.save(ApplicationUserEntity.fromDomain(user))
        val savedDomain = savedEntity.toDomain()
        LOGGER.debug { "Saved new user in DB $savedDomain" }
        return savedDomain
    }

    override fun findByEmail(email: String): ApplicationUser {
        LOGGER.debug { "Searching user by email: $email" }
        val userEntity =
            userRepository.findByEmail(email) ?: throw UserNotFoundException("User with email $email not found")
        val domain = userEntity.toDomain()
        LOGGER.debug { "User founded by it's email: $domain" }
        return domain
    }

    override fun findById(id: UUID): ApplicationUser {
        return userRepository.findById(id)
            .map {
                val domain = it.toDomain()
                LOGGER.debug { "User founded by it's ID: $domain" }
                return@map domain
            }
            .orElseThrow { UserNotFoundException("User not found with specified ID: $id") }
    }

    override fun update(user: ApplicationUser): ApplicationUser {
        userRepository.findById(user.id)
            .orElseThrow { UserNotFoundException("User not found with specified ID: ${user.id}") }
        LOGGER.debug { "Updating user in DB with new data: $user" }
        val updatedEntity = userRepository.save(ApplicationUserEntity.fromDomain(user))
        val domain = updatedEntity.toDomain()
        LOGGER.debug { "User updated successfully $domain" }
        return domain
    }

    override fun deleteById(id: UUID) {
        userRepository.findById(id)
            .orElseThrow { UserNotFoundException("User not found with specified ID: $id") }
        LOGGER.debug { "Deleting user $id from DB" }
        userRepository.deleteById(id)
        LOGGER.debug { "User $id was deleted" }
    }

    companion object {
        private val LOGGER = KotlinLogging.logger {}
    }
}