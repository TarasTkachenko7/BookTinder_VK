package com.practicum.vkproject3.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.R
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
    private val authRepository: AuthRepository,
    private val context: Context
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
            emailErr = context.getString(R.string.auth_invalid_email)
            hasError = true
        }
        if (currentState.password.isBlank()) {
            passwordErr = context.getString(R.string.auth_enter_password)
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
                    _state.update { it.copy(isLoading = false, emailError = context.getString(R.string.auth_user_not_found), passwordError = null) }
                } else if (exception is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
                    _state.update { it.copy(isLoading = false, passwordError = context.getString(R.string.auth_wrong_password), emailError = null) }
                } else {
                    val errorMsg = exception?.let { context.getString(it.toUserFriendlyMessageRes()) } ?: context.getString(R.string.auth_unknown_error)
                    _state.update { it.copy(isLoading = false, passwordError = errorMsg, emailError = null) }
                }
            }
        }
    }
}
