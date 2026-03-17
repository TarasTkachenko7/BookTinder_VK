package com.practicum.vkproject3.domain.model

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null
)