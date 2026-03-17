package com.practicum.vkproject3.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.data.network.model.RegisterRequest
import com.practicum.vkproject3.domain.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

class RegistrationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegistrationState())
    val state = _state.asStateFlow()

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue, emailError = null) }
    }

    fun onPasswordChange(newValue: String) {
        _state.update { it.copy(password = newValue, passwordError = null) }
    }

    fun onConfirmPasswordChange(newValue: String) {
        _state.update { it.copy(confirmPassword = newValue, confirmPasswordError = null) }
    }

    fun register() {
        val currentState = _state.value
        var hasError = false

        if (!currentState.email.contains("@") || currentState.email.isBlank()) {
            _state.update { it.copy(emailError = "Некорректный формат почты") }
            hasError = true
        }

        if (currentState.password.length < 8) {
            _state.update { it.copy(passwordError = "Пароль не должен быть короче 8 символов") }
            hasError = true
        }

        if (currentState.password != currentState.confirmPassword) {
            _state.update { it.copy(confirmPasswordError = "Пароли не совпадают") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val request = RegisterRequest(
                email = currentState.email,
                password = currentState.password
            )
            val result = authRepository.register(request)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Произошла неизвестная ошибка"
                _state.update { it.copy(isLoading = false, emailError = errorMsg) }
            }
        }
    }
}