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

data class VerificationState(
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val error: String? = null,
    val isResent: Boolean = false
)

class VerificationViewModel(
    private val authRepository: AuthRepository,
    private val context: Context
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
                _state.update { it.copy(isLoading = false, error = context.getString(R.string.verification_email_verified)) }
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
                val exception = result.exceptionOrNull()
                val errorMsg = if (exception is com.google.firebase.FirebaseTooManyRequestsException) {
                    context.getString(R.string.auth_reset_too_many)
                } else {
                    context.getString(R.string.auth_reset_failed)
                }
                _state.update { it.copy(isLoading = false, error = errorMsg) }
            }
        }
    }
}
