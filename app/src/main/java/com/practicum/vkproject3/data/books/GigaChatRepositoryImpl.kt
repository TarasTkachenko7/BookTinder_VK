package com.practicum.vkproject3.data.books

import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.vkproject3.BuildConfig
import com.practicum.vkproject3.data.model.FirebaseBook
import com.practicum.vkproject3.data.network.api.GigaChatApi
import com.practicum.vkproject3.data.network.model.GigaChatMessage
import com.practicum.vkproject3.data.network.model.GigaChatRequest
import com.practicum.vkproject3.domain.books.GigaChatRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GigaChatRepositoryImpl(
    private val api: GigaChatApi
) : GigaChatRepository {
    private val authKey = BuildConfig.GIGA_CHAT_KEY
    private var cachedToken: String? = null
    private var tokenExpiresAt: Long = 0
    private val gson = Gson()

    private val booksRef = FirebaseDatabase.getInstance().getReference("books")

    private val genreNames = mapOf(
        "fantasy" to "Фантастика",
        "detective" to "Детектив",
        "romance" to "Роман",
        "adventure" to "Приключения",
        "drama" to "Драма",
        "classic" to "Классика",
        "horror" to "Ужасы",
        "psychology" to "Психология",
        "science" to "Научпоп",
        "business" to "Бизнес"
    )

    override suspend fun getRecommendations(
        genres: Set<String>,
        alreadyShownIds: List<String>
    ): Result<List<Book>> {
        return try {
            val token = getValidToken()

            val snapshot = booksRef.get().await()
            val allBooks = snapshot.children.mapNotNull { it.getValue(FirebaseBook::class.java) }

            val filteredBooks = allBooks.filter { book ->
                val rusGenre = genreNames[book.genreId] ?: book.genreId
                rusGenre in genres && book.id !in alreadyShownIds
            }

            if (filteredBooks.isEmpty()) {
                return Result.success(emptyList())
            }

            val booksCatalog = filteredBooks.joinToString("\n") {
                val rusGenre = genreNames[it.genreId] ?: it.genreId
                "${it.id} | ${it.title} | ${it.author} | $rusGenre"
            }

            val genresString = genres.joinToString(", ")
            val maxToRequest = minOf(10, filteredBooks.size)

            val promptText = """
                Ты — профессиональная рекомендательная система книг. Пользователь любит жанры: $genresString.
                Вот каталог доступных книг, которые уже отфильтрованы под его вкусы:
                
                $booksCatalog
                
                Выбери $maxToRequest самых подходящих книг из этого списка, распределив их по возможности равномерно между любимыми жанрами.
                Верни ответ СТРОГО в формате валидного JSON-массива строк, содержащего ТОЛЬКО ID выбранных книг. Никакого другого текста быть не должно.
                Пример формата:
                ["b1", "b15", "b42"]
            """.trimIndent()

            val request = GigaChatRequest(
                model = "GigaChat",
                messages = listOf(
                    GigaChatMessage(role = "system", content = "Ты полезный ассистент, который выдает ответы строго в JSON массиве строк."),
                    GigaChatMessage(role = "user", content = promptText)
                ),
                temperature = 0.7
            )

            val response = api.getCompletions(
                bearerToken = "Bearer $token",
                request = request
            )

            val rawContent = response.choices.firstOrNull()?.message?.content ?: ""
            val cleanJson = rawContent.replace("```json", "").replace("```", "").trim()
            val listType = object : TypeToken<List<String>>() {}.type
            val recommendedIds: List<String> = Gson().fromJson(cleanJson, listType)

            val finalBooks = allBooks
                .filter { it.id in recommendedIds }
                .map { firebaseBook ->
                    firebaseBook.toDomainBook(firebaseBook.genreId)
                }
                .shuffled()

            Result.success(finalBooks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getValidToken(): String {
        val currentTime = System.currentTimeMillis()
        if (cachedToken != null && currentTime < (tokenExpiresAt - 60000)) {
            return cachedToken!!
        }

        val rqUid = UUID.randomUUID().toString()
        val response = api.getToken(
            authHeader = "Basic $authKey",
            rqUid = rqUid
        )

        cachedToken = response.accessToken
        tokenExpiresAt = response.expiresAt

        return response.accessToken
    }
}