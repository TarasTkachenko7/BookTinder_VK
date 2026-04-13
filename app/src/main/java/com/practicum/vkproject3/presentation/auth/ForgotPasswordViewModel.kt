package com.practicum.vkproject3.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.domain.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ForgotStep { EMAIL, SUCCESS }

data class ForgotPasswordState(
    val step: ForgotStep = ForgotStep.EMAIL,
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val errorMessage: String? = null
)

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun submitEmail() {
        val email = _state.value.email
        if (!email.contains("@") || email.isBlank()) {
            _state.update { it.copy(emailError = "Введите корректную почту") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, step = ForgotStep.SUCCESS) }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Ошибка при отправке письма"
                _state.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        }
    }
}
