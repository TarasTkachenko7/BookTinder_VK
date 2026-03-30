package com.practicum.vkproject3.data.profile

import android.content.Context
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.UserProfile
import com.practicum.vkproject3.domain.profile.UserRepository
import kotlinx.coroutines.delay

class UserRepositoryImpl(
    private val context: Context,
    private val genreManager: UserGenreManager
) : UserRepository {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override suspend fun getProfile(): UserProfile {
        delay(500)
        val name = prefs.getString("user_name", UserSession.userName ?: context.getString(R.string.user_default_name)) ?: ""
        val avatarUrl = prefs.getString("user_avatar", UserSession.userAvatarUrl)
        val genres = prefs.getStringSet("user_genres", UserSession.selectedGenres) ?: emptySet()

        return UserProfile(
            name = name,
            email = context.getString(R.string.user_default_email),
            phone = context.getString(R.string.user_default_phone),
            avatarUrl = avatarUrl,
            favoriteGenres = genres.toList()
        )
    }

    override suspend fun updateProfile(name: String, genres: List<String>, avatarUrl: String?): Boolean {
        delay(500)

        genreManager.saveUserGenres(genres)

        prefs.edit().apply {
            putString("user_name", name)
            putString("user_avatar", avatarUrl)
            putStringSet("user_genres", genres.toSet())
            apply()
        }

        UserSession.userName = name
        UserSession.selectedGenres = genres.toSet()
        UserSession.userAvatarUrl = avatarUrl
        return true
    }

    override suspend fun restorePassword(email: String): Boolean {
        delay(1500)
        return email.contains("@")
    }
}