package com.practicum.vkproject3.domain.books

data class AiBookRecommendation(
    val title: String,
    val author: String,
    val genre: String
)

interface GigaChatRepository {
    suspend fun getRecommendations(
        genres: Set<String>,
        alreadyShownTitles: List<String>
    ): Result<List<AiBookRecommendation>>
}