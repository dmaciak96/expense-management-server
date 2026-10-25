package com.example.expense_management_server.domain.authentication.port

import com.example.expense_management_server.domain.authentication.model.RefreshToken
import java.util.*

interface RefreshTokenPersistencePort {
    fun create(refreshToken: RefreshToken): RefreshToken
    fun findByToken(token: String): RefreshToken
    fun delete(token: String)
    fun deleteAllByUserId(userId: UUID)
}