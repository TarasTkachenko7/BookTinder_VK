package com.practicum.vkproject3.domain.profile
import com.practicum.vkproject3.domain.model.UserProfile

interface UserRepository {
    suspend fun getProfile(): UserProfile
    suspend fun restorePassword(email: String): Boolean
}
