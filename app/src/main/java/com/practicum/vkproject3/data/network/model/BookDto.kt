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
    @SerializedName("cover_i") val coverI: Int?
)
