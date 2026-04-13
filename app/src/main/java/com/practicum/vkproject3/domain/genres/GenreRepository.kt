package com.practicum.vkproject3.domain.genres

import com.practicum.vkproject3.domain.model.Genre

interface GenreRepository {
    suspend fun getAllGenres(): List<Genre>
}