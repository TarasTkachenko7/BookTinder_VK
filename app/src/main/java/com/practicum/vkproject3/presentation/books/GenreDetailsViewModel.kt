package com.practicum.vkproject3.presentation.books

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GenreDetailsState(
    val isLoading: Boolean = true,
    val books: List<Book> = emptyList(),
    val error: String? = null
)

class GenreDetailsViewModel(
    private val genre: String,
    private val repository: BookRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(GenreDetailsState())
    val state = _state.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val books = repository.getBooksByGenre(genre, limit = 10)

            if (books.isEmpty()) {
                _state.update { it.copy(isLoading = false, error = context.getString(R.string.error_no_books_for_genre)) }
            } else {
                _state.update { it.copy(isLoading = false, books = books, error = null) }
            }
        }
    }
}
