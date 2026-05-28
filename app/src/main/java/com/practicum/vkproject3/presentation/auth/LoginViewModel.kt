package com.practicum.vkproject3.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.data.network.model.AuthRequest
import com.practicum.vkproject3.domain.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(newValue: String) {
        _state.update { it.copy(email = newValue, emailError = null) }
    }

    fun onPasswordChange(newValue: String) {
        _state.update { it.copy(password = newValue, passwordError = null) }
    }

    fun login() {
        val currentState = _state.value

        var hasError = false
        var emailErr: String? = null
        var passwordErr: String? = null

        if (currentState.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            emailErr = "Некорректный формат email"
            hasError = true
        }
        if (currentState.password.isBlank()) {
            passwordErr = "Введите пароль"
            hasError = true
        }

        if (hasError) {
            _state.update { it.copy(emailError = emailErr, passwordError = passwordErr) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val request = AuthRequest(
                email = currentState.email,
                password = currentState.password
            )
            val result = authRepository.login(request)

            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else {
                val exception = result.exceptionOrNull()
                if (exception is com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                    _state.update { it.copy(isLoading = false, emailError = "Такого пользователя не существует", passwordError = null) }
                } else if (exception is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                    _state.update { it.copy(isLoading = false, passwordError = "Неверный пароль", emailError = null) }
                } else {
                    val errorMsg = exception?.toUserFriendlyMessage() ?: "Произошла неизвестная ошибка. Повторите попытку"
                    _state.update { it.copy(isLoading = false, passwordError = errorMsg, emailError = null) }
                }
            }
        }
    }
}
