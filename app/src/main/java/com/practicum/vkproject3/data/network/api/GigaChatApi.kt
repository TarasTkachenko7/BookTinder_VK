package com.practicum.vkproject3.data.network.api

import com.practicum.vkproject3.data.network.model.GigaChatAuthResponse
import com.practicum.vkproject3.data.network.model.GigaChatRequest
import com.practicum.vkproject3.data.network.model.GigaChatResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface GigaChatApi {
    @FormUrlEncoded
    @POST("https://ngw.devices.sberbank.ru:9443/api/v2/oauth")
    suspend fun getToken(
        @Header("Authorization") authHeader: String,
        @Header("RqUID") rqUid: String,
        @Field("scope") scope: String = "GIGACHAT_API_PERS"
    ): GigaChatAuthResponse

    @POST("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
    suspend fun getCompletions(
        @Header("Authorization") bearerToken: String,
        @Body request: GigaChatRequest
    ): GigaChatResponse
}