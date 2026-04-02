package com.practicum.vkproject3.data.network.api

import com.practicum.vkproject3.data.network.model.AuthRequest
import com.practicum.vkproject3.data.network.model.AuthResponse
import com.practicum.vkproject3.data.network.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>
}
