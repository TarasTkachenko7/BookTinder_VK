package com.practicum.vkproject3.domain.profile
import com.practicum.vkproject3.domain.model.UserProfile

interface UserRepository {
    suspend fun getProfile(): UserProfile
    suspend fun updateProfile(name: String, genres: List<String>, avatarUrl: String?): Boolean
    suspend fun restorePassword(email: String): Boolean
}
