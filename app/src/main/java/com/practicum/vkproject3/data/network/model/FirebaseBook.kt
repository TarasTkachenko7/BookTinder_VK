package com.practicum.vkproject3.data.model

import android.net.Uri
import com.practicum.vkproject3.domain.model.Book

data class FirebaseBook(
    val id: String = "",
    val editionId: String = "",
    val title: String = "",
    val author: String = "",
    val rating: Double = 0.0,
    val genreId: String = "",
    val imageUrl: String = "",
    val description: String = ""
) {
    fun toDomainBook(genreName: String): Book {
        return Book(
            id = Uri.decode(id),
            edition_id = Uri.decode(editionId),
            title = title,
            author = author,
            imageUrl = imageUrl,
            rating = rating,
            genre = genreName,
            description = description
        )
    }
}