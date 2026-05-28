package com.practicum.vkproject3.presentation.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

data class FavoritesState(
    val favorites: List<Book> = emptyList(),
    val isLoading: Boolean = true
)

class FavoritesViewModel(private val repository: BookRepository) : ViewModel() {
    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeFavoriteBooks().collectLatest { books ->
                _state.update { it.copy(favorites = books, isLoading = false) }
            }
        }
    }

    fun removeFavorite(bookId: String) {
        viewModelScope.launch {
            repository.removeFavoriteBook(bookId)
        }
    }
}
