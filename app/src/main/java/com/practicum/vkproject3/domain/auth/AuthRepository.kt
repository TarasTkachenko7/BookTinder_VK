package com.practicum.vkproject3.domain.auth

import com.practicum.vkproject3.data.network.model.AuthRequest
import com.practicum.vkproject3.data.network.model.AuthResponse
import com.practicum.vkproject3.data.network.model.RegisterRequest

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun login(request: AuthRequest): Result<AuthResponse>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun logout()
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun isEmailVerified(): Boolean
    suspend fun deleteAccount(): Result<Unit>
}
