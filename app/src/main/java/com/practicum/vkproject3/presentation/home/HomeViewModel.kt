package com.practicum.vkproject3.presentation.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.data.profile.UserSession
import com.practicum.vkproject3.domain.books.BookRepository
import com.practicum.vkproject3.domain.books.GigaChatRepository
import com.practicum.vkproject3.domain.model.Book
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
    val isFavorite: Boolean,
    val description: String
)

data class HomeState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val books: List<HomeBookUi> = emptyList(),
    val index: Int = 0,
    val isExhausted: Boolean = false
) {
    val current: HomeBookUi? get() = books.getOrNull(index)
    val isEmpty: Boolean get() = !isLoading && error == null && books.isEmpty()
}

class HomeViewModel(
    private val repository: BookRepository,
    private val aiRepository: GigaChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadAiBooks()
    }

    fun loadAiBooks() {
        if (_state.value.isLoading && _state.value.books.isNotEmpty()) return

        viewModelScope.launch {
            if (_state.value.isExhausted) return@launch

            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val genres = UserSession.selectedGenres.ifEmpty { setOf("Фантастика", "Роман") }
                val alreadyShownIds = _state.value.books.map { it.id }.distinct()

                val aiResult = aiRepository.getRecommendations(genres, alreadyShownIds)

                if (aiResult.isSuccess) {
                    val domainBooks = aiResult.getOrNull() ?: emptyList()
                    val newUiBooks = domainBooks.map { it.toHomeUi() }

                    if (newUiBooks.isNotEmpty()) {
                        _state.update { st ->
                            st.copy(
                                isLoading = false,
                                books = st.books + newUiBooks
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false, isExhausted = true) }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Ошибка генерации рекомендаций") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Проверьте подключение к сети") }
            }
        }
    }

    fun onPageChanged(newIndex: Int) {
        _state.update { it.copy(index = newIndex) }
    }

    fun next() {
        _state.update { st ->
            if (st.books.isEmpty()) return@update st

            val newIndex = minOf(st.index + 1, st.books.size)

            if (newIndex >= st.books.size - 3 && !st.isLoading && !st.isExhausted) {
                loadAiBooks()
            }

            st.copy(index = newIndex)
        }
    }

    fun prev() {
        _state.update { st ->
            if (st.books.isEmpty()) st
            else st.copy(index = (st.index - 1).coerceAtLeast(0))
        }
    }

    fun toggleFavorite() {
        val cur = _state.value.current ?: return

        viewModelScope.launch {
            val domainBook = Book(
                id = cur.id,
                title = cur.title,
                author = cur.author,
                imageUrl = cur.coverUrl,
                rating = cur.rating.toDouble(),
                genre = cur.genreId,
                description = cur.description,
                edition_id = cur.editionId
            )

            val success = if (cur.isFavorite) {
                repository.removeFromFavorites(Uri.encode(cur.id))
            } else {
                repository.addToFavorites(domainBook)
            }

            if (success) {
                _state.update { st ->
                    val updatedBooks = st.books.map {
                        if (it.id == cur.id) it.copy(isFavorite = !it.isFavorite) else it
                    }
                    st.copy(books = updatedBooks)
                }
            }
        }
    }

    private fun Book.toHomeUi(): HomeBookUi {
        val displayRating = if (rating > 0) rating.toFloat() else {
            ((id.hashCode().toUInt().toLong() % 21) / 10f + 3.0f)
        }
        return HomeBookUi(
            id = id,
            editionId = edition_id.toString(),
            title = title,
            author = author,
            coverUrl = imageUrl,
            genreId = genre,
            rating = displayRating,
            isFavorite = UserSession.isFavorite(id),
            description = this.description ?: "Описание отсутствует."
        )
    }
}