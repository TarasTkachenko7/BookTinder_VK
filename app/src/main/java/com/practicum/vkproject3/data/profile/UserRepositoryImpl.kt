package com.practicum.vkproject3.data.profile

import android.content.Context
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.UserProfile
import com.practicum.vkproject3.domain.profile.UserRepository
import kotlinx.coroutines.delay

class UserRepositoryImpl(private val context: Context) : UserRepository {

    override suspend fun getProfile(): UserProfile {
        delay(2000)
        return UserProfile(
            name = context.getString(R.string.user_default_name),
            email = context.getString(R.string.user_default_email),
            phone = context.getString(R.string.user_default_phone)
        )
    }

    override suspend fun restorePassword(email: String): Boolean {
        delay(1500)
        return email.contains("@")
    }
}
