package com.practicum.vkproject3.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerificationState(
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val error: String? = null,
    val isResent: Boolean = false
)

class VerificationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(VerificationState())
    val state = _state.asStateFlow()

    fun checkVerification() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val verified = authRepository.isEmailVerified()
            if (verified) {
                _state.update { it.copy(isLoading = false, isVerified = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Почта еще не подтверждена. Пожалуйста, проверьте ваше письмо.") }
            }
        }
    }

    fun resendEmail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.sendEmailVerification()
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isResent = true) }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Ошибка при отправке"
                _state.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }
}