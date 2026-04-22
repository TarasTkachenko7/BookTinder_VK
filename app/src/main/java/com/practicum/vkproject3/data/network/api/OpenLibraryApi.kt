package com.practicum.vkproject3.data.network.api

import com.practicum.vkproject3.data.network.model.AuthorNameDto
import com.practicum.vkproject3.data.network.model.BookIdResponseDto
import com.practicum.vkproject3.data.network.model.EditionResponseDto
import com.practicum.vkproject3.data.network.model.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApi {
  @Headers("User-Agent: BookTinderApp (SparksVk@mail.ru)")
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int = 3
    ): SearchResponseDto

    @GET("{bookID}.json")
    suspend fun getBookInfo(
        @Path("bookID", encoded = false) bookID: String
    ): BookIdResponseDto

    @GET("books/{editionID}.json")
    suspend fun getEditionDetails(
        @Path("editionID") editionID: String?
    ): EditionResponseDto

    @GET("{authorID}.json")
    suspend fun getAuthorInfo(
        @Path("authorID") authorID: String
    ): AuthorNameDto
}