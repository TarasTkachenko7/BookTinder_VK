package com.practicum.vkproject3.domain.books
import com.practicum.vkproject3.domain.model.Book

interface BookRepository {
    suspend fun getBooks(page: Int): Pair<List<Book>, Int>
    suspend fun getBooksByAiRecommendations(recommendations: List<AiBookRecommendation>): List<Book>
    suspend fun getCatalogBooksByGenres(genres: List<String>, limit: Int = 5): Map<String, List<Book>>
    suspend fun getBooksByGenre(genre: String, limit: Int = 10): List<Book>
}
