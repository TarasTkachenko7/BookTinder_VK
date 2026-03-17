package com.practicum.vkproject3.data.network.api

import com.practicum.vkproject3.data.network.model.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ): SearchResponseDto
}
