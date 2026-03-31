package com.practicum.vkproject3.presentation.genres

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.vkproject3.R
import com.practicum.vkproject3.data.profile.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.practicum.vkproject3.data.profile.UserGenreManager

data class GenrePickState(
    val isLoading: Boolean = true,
    val genres: List<GenreItem> = emptyList(),
    val selected: Set<String> = emptySet(),
    val error: String? = null
) {
    val canSubmit: Boolean get() = selected.isNotEmpty() && !isLoading
}

data class GenreItem(
    val id: String,
    val stringResId: Int
)

class GenrePickViewModel(
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
            delay(2000)

            try {
                val genreIds = context.resources.getStringArray(R.array.genre_ids)

                val nameResIds = context.resources.obtainTypedArray(R.array.genre_name_res_ids)

                val genres = try {
                    genreIds.mapIndexed { index, id ->
                        GenreItem(id, nameResIds.getResourceId(index, 0))
                    }
                } finally {
                    nameResIds.recycle()
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        genres = genres
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = context.getString(R.string.error_genre_load)
                    )
                }
            }
        }
    }

    fun saveSelectedGenres() {
        val selectedSet = _state.value.selected
        UserSession.selectedGenres = selectedSet

        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putStringSet("user_genres", selectedSet).apply()

        viewModelScope.launch {
            userGenreManager.saveUserGenres(selectedSet.toList())
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