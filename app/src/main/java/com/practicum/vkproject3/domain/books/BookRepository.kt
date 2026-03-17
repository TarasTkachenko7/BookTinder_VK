package com.practicum.vkproject3.domain.books
import com.practicum.vkproject3.domain.model.Book

interface BookRepository {
    suspend fun getBooks(page: Int): Pair<List<Book>, Int>
}
