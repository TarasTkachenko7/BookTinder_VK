package com.practicum.vkproject3.presentation.books

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Comment(
    val id: Long,
    val authorName: String,
    val text: String,
    val replies: MutableList<Comment> = mutableStateListOf()
)

sealed interface BookDetailsUiState {
    object Loading : BookDetailsUiState
    data class Success(val book: Book) : BookDetailsUiState
    data class Error(val message: String) : BookDetailsUiState
}

class BookDetailsViewModel(
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailsUiState>(BookDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadDetails(
        bookId: String,
        editionId: String
    ) {
        viewModelScope.launch {
            _uiState.value = BookDetailsUiState.Loading
            try {
                val fullBook = repository.getBookDetails(
                    bookId = bookId,
                    editionId = editionId
                )
                _uiState.value = BookDetailsUiState.Success(fullBook)
            } catch (e: Exception) {
                _uiState.value = BookDetailsUiState.Error("Не удалось загрузить детали")
            }
        }
    }
}