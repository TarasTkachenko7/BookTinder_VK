package com.practicum.vkproject3.data.books

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.practicum.vkproject3.R
import com.practicum.vkproject3.data.model.FirebaseBook
import com.practicum.vkproject3.data.network.api.OpenLibraryApi
import com.practicum.vkproject3.domain.books.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.UUID

class BookRepositoryImpl(
    private val api: OpenLibraryApi,
    private val context: Context
) : BookRepository {

    private val memoryCache = mutableMapOf<String, Book>()

    private val genreMapping = mapOf(
        "Фантастика" to "science fiction",
        "Детектив" to "mystery",
        "Роман" to "romance",
        "Приключения" to "adventure",
        "Драма" to "drama"
    )

    override suspend fun getBooks(page: Int): Pair<List<Book>, Int> = withContext(Dispatchers.IO) {
        val response = api.searchBooks(query = "language:rus", page = page)
        val books = response.docs.map { doc ->
            val book = Book(
                id = doc.key ?: "",
                title = doc.title ?: context.getString(R.string.book_no_title),
                author = doc.authorNames?.firstOrNull()
                    ?: context.getString(R.string.book_no_author),
                imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
                    ?: "",
                rating = 0.0,
                genre = context.getString(R.string.book_genre_miscellaneous),
                description = "",
                edition_id = doc.coverEditionKey,
                languages = doc.languageList
            )
            memoryCache[book.id] = book
            book
        }
        Pair(books, response.numFound)
    }

    override suspend fun getCatalogBooksByGenres(genres: List<String>, limit: Int): Map<String, List<Book>> {
        val result = mutableMapOf<String, List<Book>>()
        for (genre in genres) {
            try {
                val response = api.searchBooks(query = genre, page = 1, limit = limit)
                val books = response.docs.map { doc ->
                    val book = Book(
                        id = doc.key ?: UUID.randomUUID().toString(),
                        title = doc.title ?: context.getString(R.string.book_no_title),
                        author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                        imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" } ?: "",
                        rating = ((doc.key?.hashCode()?.toUInt()?.toLong() ?: 0L) % 21) / 10.0 + 3.0,
                        genre = genre,
                        description = ""
                    )
                    memoryCache[book.id] = book // Сохраняем в кеш
                    book
                }
                if (books.isNotEmpty()) {
                    result[genre] = books
                }
                delay(500)
            } catch (e: Exception) {
                Log.e("CatalogNetwork", "Ошибка при загрузке жанра $genre: ${e.message}")
            }
        }
        return result
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
    override suspend fun getBooksByGenre(genre: String, limit: Int): List<Book> {
        return try {
            val englishTag = genreMapping[genre] ?: "fiction"
            val response = api.searchBooks(query = englishTag, page = 1, limit = limit)
            response.docs.map { doc ->
                val titleStr = doc.title ?: context.getString(R.string.book_no_title)
                val encodedTitle = URLEncoder.encode(titleStr, "UTF-8")
                val fallbackCoverUrl = "https://ui-avatars.com/api/?name=$encodedTitle&background=2C3E34&color=fff&size=512&font-size=0.3"
                val book = Book(
                    id = doc.key ?: UUID.randomUUID().toString(),
                    title = titleStr,
                    author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                    imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" } ?: fallbackCoverUrl,
                    rating = ((doc.key?.hashCode()?.toUInt()?.toLong() ?: 0L) % 21) / 10.0 + 3.0,
                    genre = genre,
                    description = ""
                )
                memoryCache[book.id] = book
                book
            }
        } catch (e: Exception) {
            Log.e("GenreDetails", "Ошибка загрузки жанра $genre: ${e.message}")
            emptyList()
        }
    }

    private val booksRef = FirebaseDatabase.getInstance().getReference("books")

    suspend fun getAllBooksFromDatabase(): List<FirebaseBook> {
        return try {
            val snapshot = booksRef.get().await()
            val booksList = mutableListOf<FirebaseBook>()

            for (childSnapshot in snapshot.children) {
                val book = childSnapshot.getValue(FirebaseBook::class.java)
                if (book != null) {
                    booksList.add(book)
                }
            }
            booksList
        } catch (e: Exception) {
            Log.e("FirebaseData", "Ошибка загрузки книг: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getBookById(id: String): Book? = withContext(Dispatchers.IO) {
        memoryCache[id]?.let { return@withContext it }

        try {
            val firebaseBooks = getAllBooksFromDatabase()
            val fBook = firebaseBooks.find { it.id == id }

            if (fBook != null) {
                return@withContext Book(
                    id = fBook.id ?: id,
                    title = fBook.title ?: context.getString(R.string.book_no_title),
                    author = fBook.author ?: context.getString(R.string.book_no_author),
                    imageUrl = fBook.imageUrl ?: "",
                    rating = fBook.rating?.toString()?.toDoubleOrNull() ?: 0.0,
                    genre = fBook.genreId ?: "Неизвестный жанр",
                    description = fBook.description ?: "Описание отсутствует."
                )
            }
        } catch (e: Exception) {
            Log.e("BookRepository", "Ошибка при поиске в Firebase: ${e.message}")
        }

        try {
            val cleanId = id.substringAfterLast("/")
            val response = api.searchBooks(query = cleanId, page = 1, limit = 5)

            val doc = response.docs.find { it.key == id || it.key?.contains(cleanId) == true }

            if (doc != null) {
                val titleStr = doc.title ?: context.getString(R.string.book_no_title)
                val encodedTitle = URLEncoder.encode(titleStr, "UTF-8")
                val fallbackCoverUrl = "https://ui-avatars.com/api/?name=$encodedTitle&background=2C3E34&color=fff&size=512&font-size=0.3"

                return@withContext Book(
                    id = doc.key ?: id,
                    title = titleStr,
                    author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                    imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: fallbackCoverUrl,
                    rating = ((doc.key?.hashCode()?.toUInt()?.toLong() ?: 0L) % 21) / 10.0 + 3.0,
                    genre = "Разное",
                    description = "Описание отсутствует."
                )
            }
        } catch (e: Exception) {
            Log.e("BookRepository", "Ошибка при поиске в OpenLibrary: ${e.message}")
        }

        return@withContext null
    }
}