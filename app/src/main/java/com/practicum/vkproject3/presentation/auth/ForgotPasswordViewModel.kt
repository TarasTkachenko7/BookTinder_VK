package com.practicum.vkproject3.presentation.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.vkproject3.R
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
    private val authRepository: AuthRepository,
    private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, emailError = null, errorMessage = null) }
    }

    fun submitEmail() {
        val email = _state.value.email
        if (!email.contains("@") || email.isBlank()) {
            _state.update { it.copy(emailError = context.getString(R.string.auth_reset_email_invalid)) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, step = ForgotStep.SUCCESS) }
            } else {
                val errorMsg = result.exceptionOrNull()?.let { context.getString(it.toUserFriendlyMessageRes()) } ?: context.getString(R.string.auth_unknown_error)
                _state.update { it.copy(isLoading = false, errorMessage = errorMsg) }
            }
        }
    }
}
