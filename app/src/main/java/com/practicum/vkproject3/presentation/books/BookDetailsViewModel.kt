package com.practicum.vkproject3.presentation.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookDetailsState(
    val isLoading: Boolean = true,
    val book: Book? = null,
    val error: String? = null
)

class BookDetailsViewModel(private val repository: BookRepository) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailsState())
    val state = _state.asStateFlow()

    fun loadBook(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val loadedBook = repository.getBookById(id)
                _state.update { it.copy(isLoading = false, book = loadedBook) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}