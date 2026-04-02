package com.practicum.vkproject3.data.books

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.vkproject3.data.network.api.GigaChatApi
import com.practicum.vkproject3.data.network.model.GigaChatMessage
import com.practicum.vkproject3.data.network.model.GigaChatRequest
import com.practicum.vkproject3.domain.books.AiBookRecommendation
import com.practicum.vkproject3.domain.books.GigaChatRepository
import java.util.UUID
import com.practicum.vkproject3.BuildConfig

class GigaChatRepositoryImpl(
    private val api: GigaChatApi
) : GigaChatRepository {
    private val authKey = BuildConfig.GIGA_CHAT_KEY
    private var cachedToken: String? = null
    private var tokenExpiresAt: Long = 0
    private val gson = Gson()

    override suspend fun getRecommendations(
        genres: Set<String>,
        alreadyShownTitles: List<String>
    ): Result<List<AiBookRecommendation>> {
        return try {
            val token = getValidToken()
            val genresString = genres.joinToString(", ")
            val excludeString = if (alreadyShownTitles.isNotEmpty()) {
                "КАТЕГОРИЧЕСКИ ЗАПРЕЩАЕТСЯ предлагать следующие книги (пользователь их уже видел): ${alreadyShownTitles.joinToString("; ")}."
            } else ""

            val promptText = """
                Ты — профессиональная рекомендательная система книг. Пользователь любит жанры: $genresString.
                Предложи ровно 20 книг, которые идеально подходят под эти жанры. Постарайся равномерно охватить все перечисленные жанры.
                $excludeString
                Верни ответ СТРОГО в формате валидного JSON-массива объектов, без любого другого текста.
                Пример формата:
                [
                  {"title": "Название книги", "author": "Имя Автора", "genre": "ОДИН_ЖАНР_ИЗ_СПИСКА_ВЫШЕ"}
                ]
            """.trimIndent()

            val request = GigaChatRequest(
                model = "GigaChat",
                messages = listOf(
                    GigaChatMessage(role = "system", content = "Ты полезный ассистент, который выдает ответы строго в JSON массиве."),
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
            val listType = object : TypeToken<List<AiBookRecommendation>>() {}.type
            val recommendations: List<AiBookRecommendation> = Gson().fromJson(cleanJson, listType)

            Result.success(recommendations)
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