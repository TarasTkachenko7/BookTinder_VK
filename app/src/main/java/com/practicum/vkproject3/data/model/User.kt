package com.practicum.vkproject3.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val avatarUrl: String? = null,
    val favoriteGenres: List<String> = emptyList()
)
