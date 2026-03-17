package com.practicum.vkproject3.data.network.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String? = null
)
