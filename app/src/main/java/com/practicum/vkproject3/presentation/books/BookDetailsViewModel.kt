package com.practicum.vkproject3.presentation.books

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Comment(
    val id: Long,
    val authorName: String,
    val text: String,
    val replies: MutableList<Comment> = mutableStateListOf()
)

sealed interface BookDetailsUiState {
    object Loading : BookDetailsUiState
    data class Success(val book: Book, val isFavorite: Boolean) : BookDetailsUiState
    data class Error(val message: String) : BookDetailsUiState
}

class BookDetailsViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailsUiState>(BookDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadDetails(bookId: String, editionId: String) {
        viewModelScope.launch {
            _uiState.value = BookDetailsUiState.Loading
            try {
                val fullBook = repository.getBookDetails(bookId, editionId)
                val isFav = repository.isBookFavorite(bookId)

                _uiState.value = BookDetailsUiState.Success(fullBook, isFav)
            } catch (e: Exception) {
                _uiState.value = BookDetailsUiState.Error("Ошибка загрузки")
            }
        }
    }

    fun toggleFavorite(book: Book) {
        val currentState = _uiState.value
        if (currentState is BookDetailsUiState.Success) {
            viewModelScope.launch {
                val newStatus = !currentState.isFavorite
                val success = if (newStatus) repository.addToFavorites(book)
                else repository.removeFromFavorites(book.id)

                if (success) {
                    _uiState.value = currentState.copy(isFavorite = newStatus)
                }
            }
        }
    }
}