package com.practicum.vkproject3.domain.books

import com.practicum.vkproject3.domain.model.Book

interface BookRepository {
    suspend fun getBooks(page: Int): Pair<List<Book>, Int>
    suspend fun getCatalogBooksByGenres(genres: List<String>, limit: Int): Map<String, List<Book>>
    suspend fun getBooksByGenre(genre: String, limit: Int): List<Book>
}