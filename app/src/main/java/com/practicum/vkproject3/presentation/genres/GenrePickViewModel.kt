package com.practicum.vkproject3.presentation.genres

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.data.profile.UserGenreManager
import com.practicum.vkproject3.data.profile.UserSession
import com.practicum.vkproject3.domain.genres.GenreRepository
import com.practicum.vkproject3.domain.model.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GenrePickState(
    val isLoading: Boolean = true,
    val genres: List<Genre> = emptyList(),
    val selected: Set<String> = emptySet(),
    val error: String? = null
) {
    val canSubmit: Boolean get() = selected.isNotEmpty() && !isLoading
}

class GenrePickViewModel(
    private val repository: GenreRepository,
    private val context: Context,
    private val userGenreManager: UserGenreManager
) : ViewModel() {
    private val _state = MutableStateFlow(GenrePickState(selected = UserSession.selectedGenres))
    val state = _state.asStateFlow()

    init {
        loadGenres()
    }

    fun loadGenres() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val genresFromDb = repository.getAllGenres()

                _state.update {
                    it.copy(
                        isLoading = false,
                        genres = genresFromDb
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Не удалось загрузить жанры"
                    )
                }
            }
        }
    }

    fun saveSelectedGenres() {
        val selectedNames = _state.value.genres
            .filter { it.id in _state.value.selected }
            .map { it.name }
            .toSet()

        val finalSet = if (selectedNames.isNotEmpty()) selectedNames else _state.value.selected

        UserSession.selectedGenres = finalSet

        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("user_genres", finalSet).apply()

        viewModelScope.launch {
            userGenreManager.saveUserGenres(finalSet.toList())
        }
    }

    fun toggleGenre(id: String) {
        _state.update { st ->
            val next = st.selected.toMutableSet()
            if (!next.add(id)) next.remove(id)
            st.copy(selected = next)
        }
    }
}