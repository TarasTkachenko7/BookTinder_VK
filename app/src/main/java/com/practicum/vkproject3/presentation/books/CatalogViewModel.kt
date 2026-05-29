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

data class CatalogGenreRow(
    val genreName: String,
    val books: List<Book>
)

data class CatalogState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val allGenreRows: List<CatalogGenreRow> = emptyList(),
    val filteredBooks: List<Book> = emptyList(),
    val error: String? = null
)

class CatalogViewModel(
    private val repository: BookRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogState())
    val state = _state.asStateFlow()

    private val catalogGenresMap = mapOf(
        "fantasy" to context.getString(R.string.genre_fantasy),
        "detective" to context.getString(R.string.genre_detective),
        "romance" to context.getString(R.string.genre_romance),
        "adventure" to context.getString(R.string.genre_adventure),
        "drama" to context.getString(R.string.genre_drama)
    )

    init {
        loadCatalog()
    }

    private fun loadCatalog() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val apiGenres = catalogGenresMap.keys.toList()
                val booksMap = repository.getCatalogBooksByGenres(apiGenres, limit = 5)

                val rows = booksMap.map { (apiGenre, books) ->
                    CatalogGenreRow(catalogGenresMap[apiGenre] ?: apiGenre, books)
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        allGenreRows = rows,
                        filteredBooks = emptyList()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = context.getString(R.string.error_load_catalog)) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { currentState ->
            val filtered = if (query.isBlank()) {
                emptyList()
            } else {
                currentState.allGenreRows
                    .flatMap { it.books }
                    .filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.author.contains(query, ignoreCase = true)
                    }
                    .distinctBy { it.id }
            }
            currentState.copy(searchQuery = query, filteredBooks = filtered)
        }
    }
}