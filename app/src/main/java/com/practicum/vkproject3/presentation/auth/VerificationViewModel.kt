package com.practicum.vkproject3.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerificationState(
    val code: String = "",
    val isLoading: Boolean = false,
    val isVerified: Boolean = false,
    val error: String? = null
)

class VerificationViewModel : ViewModel() {
    private val _state = MutableStateFlow(VerificationState())
    val state = _state.asStateFlow()

    fun onCodeChange(newCode: String) {
        if (newCode.length <= 6) {
            _state.update { it.copy(code = newCode, error = null) }
        }
    }

    fun verify() {
        val currentCode = _state.value.code

        if (currentCode.length < 6) {
            _state.update { it.copy(error = "Введите 6 цифр") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            delay(1500)

            if (currentCode == "111111") {
                _state.update { it.copy(isLoading = false, isVerified = true) }
            } else {
                _state.update { it.copy(isLoading = false, error = "Неверный код") }
            }
        }
    }
}