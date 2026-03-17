package com.practicum.vkproject3.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ForgotStep { EMAIL, CODE, NEW_PASSWORD, SUCCESS }

data class ForgotPasswordState(
    val step: ForgotStep = ForgotStep.EMAIL,
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val codeError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) { _state.update {
        it.copy(email = email, emailError = null) }
    }
    fun onCodeChange(code: String) { if (code.length <= 4) _state.update {
        it.copy(code = code, codeError = null) }
    }
    fun onNewPasswordChange(pass: String) { _state.update {
        it.copy(newPassword = pass, passwordError = null) }
    }
    fun onConfirmPasswordChange(pass: String) { _state.update {
        it.copy(confirmPassword = pass, confirmPasswordError = null) }
    }

    fun submitEmail() {
        if (!_state.value.email.contains("@")) {
            _state.update { it.copy(emailError = "Введите корректную почту") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000)
            _state.update { it.copy(isLoading = false, step = ForgotStep.CODE) }
        }
    }

    fun submitCode() {
        if (_state.value.code == "5478") {
            _state.update { it.copy(step = ForgotStep.NEW_PASSWORD, codeError = null) }
        } else {
            _state.update { it.copy(codeError = "Неверный код подтверждения") }
        }
    }

    fun submitNewPassword() {
        val s = _state.value
        var hasError = false
        if (s.newPassword.length < 8) {
            _state.update { it.copy(passwordError = "Пароль должен быть не менее 8 символов") }
            hasError = true
        }
        if (s.newPassword != s.confirmPassword) {
            _state.update { it.copy(confirmPasswordError = "Пароли не совпадают") }
            hasError = true
        }
        if (hasError) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500)
            _state.update { it.copy(isLoading = false, step = ForgotStep.SUCCESS) }
        }
    }
}