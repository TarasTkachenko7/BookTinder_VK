package com.practicum.vkproject3.data.books

import android.content.Context
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.Book
import com.practicum.vkproject3.data.network.api.OpenLibraryApi
import com.practicum.vkproject3.domain.books.BookRepository

class BookRepositoryImpl(
    private val api: OpenLibraryApi,
    private val context: Context
) : BookRepository {
    override suspend fun getBooks(page: Int): Pair<List<Book>, Int> {
        val response = api.searchBooks(query = "language:rus", page = page)
        val books = response.docs.map { doc ->
            Book(
                id = doc.key ?: "",
                title = doc.title ?: context.getString(R.string.book_no_title),
                author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: "",
                rating = 0.0,
                genre = context.getString(R.string.book_genre_miscellaneous)
            )
        }
        return Pair(books, response.numFound)
    }
}
