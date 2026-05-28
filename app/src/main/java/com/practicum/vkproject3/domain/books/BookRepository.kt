package com.practicum.vkproject3.domain.books

import com.practicum.vkproject3.domain.model.Book

import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooks(page: Int): Pair<List<Book>, Int>
    suspend fun getCatalogBooksByGenres(genres: List<String>, limit: Int): Map<String, List<Book>>
    suspend fun getBooksByGenre(genre: String, limit: Int): List<Book>

    suspend fun getBookById(id: String): Book?
    suspend fun getAllBooks(): List<Book>
    suspend fun getPagedBooks(limit: Int, lastKey: String?): Pair<List<Book>, String?>

    suspend fun saveFavoriteBook(book: Book)
    suspend fun removeFavoriteBook(bookId: String)
    fun observeFavoriteBooks(): Flow<List<Book>>
    suspend fun syncFavoritesOnStartup()
}