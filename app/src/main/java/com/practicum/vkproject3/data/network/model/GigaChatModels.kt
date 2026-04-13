package com.practicum.vkproject3.data.network.model

import com.google.gson.annotations.SerializedName

data class GigaChatAuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_at") val expiresAt: Long
)

data class GigaChatRequest(
    @SerializedName("model") val model: String = "GigaChat",
    @SerializedName("messages") val messages: List<GigaChatMessage>,
    @SerializedName("temperature") val temperature: Double = 0.7
)

data class GigaChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class GigaChatResponse(
    @SerializedName("choices") val choices: List<GigaChatChoice>
)

data class GigaChatChoice(
    @SerializedName("message") val message: GigaChatMessage
)