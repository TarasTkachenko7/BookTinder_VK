package com.practicum.vkproject3.data.books

import android.content.Context
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.practicum.vkproject3.R
import com.practicum.vkproject3.data.model.FirebaseBook
import com.practicum.vkproject3.data.network.api.OpenLibraryApi
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import com.practicum.vkproject3.domain.model.mapToDomainBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.UUID

import com.practicum.vkproject3.data.db.FavoriteBookDao
import com.practicum.vkproject3.data.db.toDomain
import com.practicum.vkproject3.data.db.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class BookRepositoryImpl(
    private val api: OpenLibraryApi,
    private val context: Context,
    private val favoriteBookDao: FavoriteBookDao
) : BookRepository {

    private val memoryCache = mutableMapOf<String, Book>()

    private val genreMapping = mapOf(
        context.getString(R.string.genre_fantasy) to "science fiction",
        context.getString(R.string.genre_detective) to "mystery",
        context.getString(R.string.genre_romance) to "romance",
        context.getString(R.string.genre_adventure) to "adventure",
        context.getString(R.string.genre_drama) to "drama"
    )

    override suspend fun getBooks(page: Int): Pair<List<Book>, Int> = withContext(Dispatchers.IO) {
        val response = api.searchBooks(query = "language:rus", page = page)
        val books = response.docs.map { doc ->
            val book = mapToDomainBook(
                id = doc.key ?: "",
                title = doc.title ?: context.getString(R.string.book_no_title),
                author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: "",
                rating = 0.0,
                genre = context.getString(R.string.book_genre_miscellaneous),
                description = context.getString(R.string.description_absent)
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
                    val book = mapToDomainBook(
                        id = doc.key ?: UUID.randomUUID().toString(),
                        title = doc.title ?: context.getString(R.string.book_no_title),
                        author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                        imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" } ?: "",
                        rating = ((doc.key?.hashCode()?.toUInt()?.toLong() ?: 0L) % 21) / 10.0 + 3.0,
                        genre = genre,
                        description = context.getString(R.string.description_absent)
                    )
                    memoryCache[book.id] = book
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

    override suspend fun getBooksByGenre(genre: String, limit: Int): List<Book> {
        return try {
            val englishTag = genreMapping[genre] ?: "fiction"
            val response = api.searchBooks(query = englishTag, page = 1, limit = limit)
            response.docs.map { doc ->
                val titleStr = doc.title ?: context.getString(R.string.book_no_title)
                val encodedTitle = URLEncoder.encode(titleStr, "UTF-8")
                val fallbackCoverUrl = "https://ui-avatars.com/api/?name=$encodedTitle&background=2C3E34&color=fff&size=512&font-size=0.3"
                val book = mapToDomainBook(
                    id = doc.key ?: UUID.randomUUID().toString(),
                    title = titleStr,
                    author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                    imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" } ?: fallbackCoverUrl,
                    rating = ((doc.key?.hashCode()?.toUInt()?.toLong() ?: 0L) % 21) / 10.0 + 3.0,
                    genre = genre,
                    description = context.getString(R.string.description_absent)
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

    override suspend fun getAllBooks(): List<Book> = withContext(Dispatchers.IO) {
        val firebaseBooks = getAllBooksFromDatabase()
        firebaseBooks.map { fBook ->
            val book = mapToDomainBook(
                id = fBook.id,
                title = fBook.title,
                author = fBook.author,
                imageUrl = fBook.imageUrl,
                rating = fBook.rating,
                genre = fBook.genreId,
                description = fBook.description
            )
            memoryCache[book.id] = book
            book
        }
    }

    override suspend fun getPagedBooks(limit: Int, lastKey: String?): Pair<List<Book>, String?> = withContext(Dispatchers.IO) {
        return@withContext try {
            var query = booksRef.orderByKey().limitToFirst(if (lastKey == null) limit else limit + 1)
            if (lastKey != null) {
                query = query.startAt(lastKey)
            }
            
            val snapshot = query.get().await()
            val booksList = mutableListOf<Book>()
            var newLastKey: String? = null
            
            var count = 0
            for (childSnapshot in snapshot.children) {
                if (lastKey != null && count == 0 && childSnapshot.key == lastKey) {
                    count++
                    continue
                }
                
                val fBook = childSnapshot.getValue(FirebaseBook::class.java)
                if (fBook != null) {
                    val book = mapToDomainBook(
                        id = fBook.id,
                        title = fBook.title,
                        author = fBook.author,
                        imageUrl = fBook.imageUrl,
                        rating = fBook.rating,
                        genre = fBook.genreId,
                        description = fBook.description
                    )
                    memoryCache[book.id] = book
                    booksList.add(book)
                    newLastKey = childSnapshot.key
                }
                count++
            }
            
            Pair(booksList, newLastKey)
        } catch (e: Exception) {
            Log.e("FirebaseData", "Ошибка загрузки страницы книг: ${e.message}")
            Pair(emptyList(), null)
        }
    }

    override suspend fun getBookById(id: String): Book? = withContext(Dispatchers.IO) {
        memoryCache[id]?.let { return@withContext it }

        try {
            val firebaseBooks = getAllBooksFromDatabase()
            val fBook = firebaseBooks.find { it.id == id }

            if (fBook != null) {
                return@withContext mapToDomainBook(
                    id = fBook.id ?: id,
                    title = fBook.title ?: context.getString(R.string.book_no_title),
                    author = fBook.author ?: context.getString(R.string.book_no_author),
                    imageUrl = fBook.imageUrl ?: "",
                    rating = fBook.rating?.toString()?.toDoubleOrNull() ?: 0.0,
                    genre = fBook.genreId ?: "unknown",
                    description = fBook.description ?: context.getString(R.string.description_absent)
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

                return@withContext mapToDomainBook(
                    id = doc.key ?: id,
                    title = titleStr,
                    author = doc.authorNames?.firstOrNull() ?: context.getString(R.string.book_no_author),
                    imageUrl = doc.coverI?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" } ?: fallbackCoverUrl,
                    rating = ((doc.key?.hashCode()?.toUInt()?.toLong() ?: 0L) % 21) / 10.0 + 3.0,
                    genre = context.getString(R.string.book_genre_miscellaneous),
                    description = context.getString(R.string.description_absent)
                )
            }
        } catch (e: Exception) {
            Log.e("BookRepository", "Ошибка при поиске в OpenLibrary: ${e.message}")
        }

        return@withContext null
    }

    private val auth = FirebaseAuth.getInstance()
    private val usersDatabase = FirebaseDatabase.getInstance().getReference("users")

    override suspend fun saveFavoriteBook(book: Book) {
        favoriteBookDao.insertBook(book.toEntity())
        
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                val fBook = com.practicum.vkproject3.data.model.FirebaseBook(
                    id = book.id,
                    title = book.title,
                    author = book.author,
                    rating = book.rating,
                    genreId = book.genre,
                    imageUrl = book.imageUrl,
                    description = book.description
                )
                usersDatabase.child(uid).child("favorites").child(book.id).setValue(fBook).await()
            } catch (e: Exception) {
                Log.e("Sync", "Failed to sync save: ${e.message}")
            }
        }
    }

    override suspend fun removeFavoriteBook(bookId: String) {
        favoriteBookDao.deleteBook(bookId)
        
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            val uid = auth.currentUser?.uid ?: return@launch
            try {
                usersDatabase.child(uid).child("favorites").child(bookId).removeValue().await()
            } catch (e: Exception) {
                Log.e("Sync", "Failed to sync delete: ${e.message}")
            }
        }
    }

    override fun observeFavoriteBooks(): Flow<List<Book>> {
        return favoriteBookDao.observeFavoriteBooks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncFavoritesOnStartup(): Unit = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: return@withContext
        try {
            val snapshot = usersDatabase.child(uid).child("favorites").get().await()
            val remoteBooks = mutableListOf<com.practicum.vkproject3.data.model.FirebaseBook>()
            for (child in snapshot.children) {
                child.getValue(com.practicum.vkproject3.data.model.FirebaseBook::class.java)?.let { remoteBooks.add(it) }
            }
            
            favoriteBookDao.deleteAll()
            val entities = remoteBooks.map { 
                Book(
                    id = it.id,
                    title = it.title,
                    author = it.author,
                    rating = it.rating,
                    genre = it.genreId,
                    imageUrl = it.imageUrl,
                    description = it.description
                ).toEntity()
            }
            favoriteBookDao.insertBooks(entities)
        } catch (e: Exception) {
            Log.e("Sync", "Failed to sync on startup: ${e.message}")
        }
    }
}
