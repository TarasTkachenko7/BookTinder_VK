package com.practicum.vkproject3.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.data.profile.UserSession
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.model.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeBookUi(
    val id: String,
    val editionId: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val genreId: String,
    val rating: Float,
    val isFavorite: Boolean
)

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val books: List<HomeBookUi> = emptyList(),
    val index: Int = 0
) {
    val current: HomeBookUi? get() = books.getOrNull(index)
    val isEmpty: Boolean get() = !isLoading && error == null && books.isEmpty()
}

class HomeViewModel(private val repository: BookRepository) : ViewModel() {


    private val _state = MutableStateFlow(HomeState(isLoading = true))
    val state = _state.asStateFlow()

    private var currentPage = 1

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            delay(2000)

            try {
                val (newBooks, _) = repository.getBooks(currentPage)

                val selectedGenreIds = UserSession.selectedGenres.toList()
                val genreIdsToUse = if (selectedGenreIds.isNotEmpty()) {
                    selectedGenreIds
                } else {
                    listOf("unknown")
                }

                val uiBooks = newBooks.mapIndexed { i, b ->
                    val genreId = genreIdsToUse[i % genreIdsToUse.size]
                    b.toHomeUi(genreId)
                }

                _state.update { st ->
                    st.copy(
                        isLoading = false,
                        books = uiBooks,
                        index = 0
                    )
                }

                currentPage++
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "ERROR_LOAD") }
            }
        }
    }

    fun onPageChanged(newIndex: Int) {
        _state.update { it.copy(index = newIndex) }
    }

    fun toggleFavorite() {
        _state.update { st ->
            val cur = st.current ?: return@update st

            val domainBook = Book(
                id = cur.id,
                title = cur.title,
                author = cur.author,
                imageUrl = cur.coverUrl,
                rating = cur.rating.toDouble(),
                genre = cur.genreId
            )

            UserSession.toggleFavorite(domainBook)

            val updated = st.books.map {
                if (it.id == cur.id) it.copy(isFavorite = !it.isFavorite) else it
            }
            st.copy(books = updated)
        }
    }

    private fun Book.toHomeUi(genreId: String): HomeBookUi {
        val displayRating = if (rating > 0) rating.toFloat() else {
            ((id.hashCode().toUInt().toLong() % 21) / 10f + 3.0f).coerceIn(1f, 5f)
        }

        return HomeBookUi(
            id = id,
            editionId = edition_id.toString(),
            title = title,
            author = author,
            coverUrl = imageUrl,
            genreId = genreId,
            rating = displayRating,
            isFavorite = false
        )
    }
}
