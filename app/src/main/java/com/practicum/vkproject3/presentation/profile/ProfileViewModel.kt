package com.practicum.vkproject3.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.auth.AuthRepository
import com.practicum.vkproject3.domain.model.UserProfile
import com.practicum.vkproject3.domain.profile.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val isLoading: Boolean = false,
    val user: UserProfile? = null,
    val error: String? = null,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(
    private val repository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = ProfileState(isLoading = true)
            try {
                val profile = repository.getProfile()
                _state.value = ProfileState(isLoading = false, user = profile)
            } catch (e: Exception) {
                _state.value = ProfileState(isLoading = false, error = "Ошибка сети")
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _state.value = _state.value.copy(isLoggedOut = true)
    }
}
