package com.practicum.vkproject3.presentation.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.R
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
    val isLoggedOut: Boolean = false,
    val isUpdateSuccess: Boolean = false
)

class ProfileViewModel(
    private val repository: UserRepository,
    private val authRepository: AuthRepository,
    private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState(isLoading = true))
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val profile = repository.getProfile()
                _state.value = _state.value.copy(isLoading = false, user = profile)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = context.getString(R.string.profile_network_error))
            }
        }
    }

    fun updateProfile(name: String, genres: List<String>, avatarUrl: String? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val success = repository.updateProfile(name, genres, avatarUrl)
                if (success) {
                    _state.value = _state.value.copy(isUpdateSuccess = true)
                    loadData()
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = context.getString(R.string.profile_update_error))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = context.getString(R.string.profile_network_error))
            }
        }
    }

    fun resetUpdateSuccess() {
        _state.value = _state.value.copy(isUpdateSuccess = false)
    }

    fun logout() {
        authRepository.logout()
        _state.value = _state.value.copy(isLoggedOut = true)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    fun deleteAccount() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = authRepository.deleteAccount()
                if (result.isSuccess) {
                    _state.value = _state.value.copy(isLoading = false, isLoggedOut = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = context.getString(R.string.profile_delete_failed))
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: context.getString(R.string.profile_error_generic)
                )
            }
        }
    }
}
