package com.practicum.vkproject3.data.books

import android.content.Context
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.Book
import com.practicum.vkproject3.data.network.api.OpenLibraryApi
import com.practicum.vkproject3.domain.books.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class BookRepositoryImpl(
    private val api: OpenLibraryApi,
    private val context: Context
) : BookRepository {
    override suspend fun getBooks(page: Int): Pair<List<Book>, Int> {
        val response = api.searchBooks(query = "language:rus", page = page)
        val books = response.docs.map { doc ->
            Book(
                id = doc.key,
                title = doc.title ?: context.getString(R.string.book_no_title),
                author = doc.authorNames?.firstOrNull()
                    ?: context.getString(R.string.book_no_author),
                imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
                    ?: "",
                rating = 0.0,
                genre = context.getString(R.string.book_genre_miscellaneous),
                edition_id = doc.coverEditionKey,
                languages = doc.languageList
            )
        }
        return Pair(books, response.numFound)
    }

    override suspend fun getBookDetails(
        bookId: String,
        editionId: String
    ): Book {
        return withContext(Dispatchers.IO) {
            val descriptionDeferred = async { api.getBookInfo(bookId) }
            val editionDeferred = async { api.getEditionDetails(editionId) }

            try {
                val descriptionDto = descriptionDeferred.await()
                val editionDto = editionDeferred.await()
                val authorKey = descriptionDto.authors?.firstOrNull()?.author?.key
                val authorName = if (authorKey != null) {
                    try {
                        api.getAuthorInfo(authorKey).name ?: context.getString(R.string.book_no_author)
                    } catch (e: Exception) {
                        context.getString(R.string.book_no_author)
                    }
                } else {
                    context.getString(R.string.book_no_author)
                }
                val bookCover = editionDto.covers?.firstOrNull() ?: 0

                val book = Book(
                    id = bookId,
                    title = descriptionDto.title ?: context.getString(R.string.book_no_title),
                    author = authorName,
                    imageUrl = "https://covers.openlibrary.org/b/id/$bookCover-L.jpg",
                    rating = 0.0,
                    genre = descriptionDto.subjects?.firstOrNull().toString(),
                    edition_id = editionId,
                    description = descriptionDto.description.toString(),
                    pages = editionDto.numberOfPages,
                    publishedDate = when(descriptionDto.firstPublishYear) {
                        is Int -> descriptionDto.firstPublishYear.toString()
                        is String -> descriptionDto.firstPublishYear.takeLast(4)
                        else -> editionDto.publishDate?.takeLast(4)
                    } ,
                    languages = listOf("ru", "eng")
                )
                book
            } catch (e: Exception) {
                throw Exception("Ошибка при загрузке деталей книги")
            }
        }
    }
}