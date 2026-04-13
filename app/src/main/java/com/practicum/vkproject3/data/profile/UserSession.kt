package com.practicum.vkproject3.data.profile

import androidx.compose.runtime.mutableStateListOf
import com.practicum.vkproject3.domain.model.Book

object UserSession {
    var userName: String? = null
    var userAvatarUrl: String? = null
    var selectedGenres: Set<String> = emptySet()

    val favorites = mutableStateListOf<Book>()

    fun toggleFavorite(book: Book) {
        if (favorites.any { it.id == book.id }) {
            favorites.removeAll { it.id == book.id }
        } else {
            favorites.add(book)
        }
    }

    fun isFavorite(bookId: String): Boolean {
        return favorites.any { it.id == bookId }
    }
}
