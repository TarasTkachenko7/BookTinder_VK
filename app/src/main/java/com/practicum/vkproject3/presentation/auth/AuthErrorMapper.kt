package com.practicum.vkproject3.presentation.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.io.IOException

fun Throwable.toUserFriendlyMessage(): String {
    return when (this) {
        is FirebaseAuthInvalidUserException -> "Неверный email или пароль"
        is FirebaseAuthInvalidCredentialsException -> {
            if (this.errorCode == "ERROR_INVALID_EMAIL") {
                "Некорректный формат email"
            } else {
                "Неверный email или пароль"
            }
        }
        is FirebaseAuthUserCollisionException -> "Пользователь с таким email уже существует"
        is FirebaseAuthWeakPasswordException -> "Слишком слабый пароль (минимум 6 символов)"
        is FirebaseNetworkException, is IOException -> "Ошибка сети. Проверьте подключение к интернету"
        else -> "Произошла неизвестная ошибка. Повторите попытку"
    }
}
