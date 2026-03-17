package com.practicum.vkproject3.presentation.books

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val endReached: Boolean = false,
    val error: String? = null
)

class BookViewModel(private val repository: BookRepository) : ViewModel() {
    private val _state = MutableStateFlow(BookState())
    val state = _state.asStateFlow()
    private var currentPage = 1

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (_state.value.isLoading || _state.value.endReached) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val (newBooks, totalFound) = repository.getBooks(currentPage)
                _state.update {
                    val updatedList = it.books + newBooks
                    it.copy(
                        books = updatedList,
                        isLoading = false,
                        endReached = updatedList.size >= totalFound || newBooks.isEmpty()
                    )
                }
                currentPage++
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}