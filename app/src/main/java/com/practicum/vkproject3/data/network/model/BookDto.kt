package com.practicum.vkproject3.data.network.model

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("num_found") val numFound: Int,
    @SerializedName("docs") val docs: List<BookDocDto>
)

data class BookDocDto(
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("author_name") val authorNames: List<String>?,
    @SerializedName("cover_i") val coverI: Int?,
    @SerializedName("cover_edition_key") val coverEditionKey: String?,
    @SerializedName("language") val languageList : List<String>?
)

data class BookIdResponseDto(
    @SerializedName("description") val description: Any?,
    @SerializedName("title") val title: String?,
    @SerializedName("first_publish_date") val firstPublishYear: Any?,
    @SerializedName("subjects") val subjects: List<String>?,
    @SerializedName("authors") val authors: List<AuthorRefDto>?
){
    fun getAuthorKeys(): List<String> {
        return authors?.mapNotNull { it.author?.key } ?: emptyList()
    }
}

data class AuthorRefDto(
    @SerializedName("author") val author: AuthorKeyDto?
)

data class AuthorKeyDto(
    @SerializedName("key") val key: String?
)

data class EditionResponseDto(
    @SerializedName("number_of_pages") val numberOfPages: Int?,
    @SerializedName("covers") val covers: List<Int>?,
    @SerializedName("publish_date") val publishDate: String?
)

data class AuthorNameDto(
    @SerializedName("name") val name: String?
)
