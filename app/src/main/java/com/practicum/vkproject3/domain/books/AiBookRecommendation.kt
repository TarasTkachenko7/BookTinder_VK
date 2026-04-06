package com.practicum.vkproject3.domain.books

import com.practicum.vkproject3.domain.model.Book

interface GigaChatRepository {
    suspend fun getRecommendations(
        genres: Set<String>,
        alreadyShownIds: List<String>
    ): Result<List<Book>>
}