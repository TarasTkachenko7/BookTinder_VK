package com.practicum.vkproject3.data.model

import com.practicum.vkproject3.domain.model.Book

data class FirebaseBook(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val rating: Double = 0.0,
    val genreId: String = "",
    val imageUrl: String = "",
    val description: String = ""
) {
    fun toDomainBook(genreName: String): Book {
        return Book(
            id = id,
            title = title,
            author = author,
            imageUrl = imageUrl,
            rating = rating,
            genre = genreName,
            description = description
        )
    }
}