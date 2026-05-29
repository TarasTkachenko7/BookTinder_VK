package com.practicum.vkproject3.presentation.auth

import com.practicum.vkproject3.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.io.IOException

fun Throwable.toUserFriendlyMessageRes(): Int {
    return when (this) {
        is FirebaseAuthInvalidUserException -> R.string.auth_user_not_found
        is FirebaseAuthInvalidCredentialsException -> {
            if (this.errorCode == "ERROR_INVALID_EMAIL") {
                R.string.auth_invalid_email
            } else {
                R.string.auth_user_not_found
            }
        }
        is FirebaseAuthUserCollisionException -> R.string.auth_email_exists
        is FirebaseAuthWeakPasswordException -> R.string.auth_password_too_weak
        is FirebaseNetworkException, is IOException -> R.string.auth_network_error
        else -> R.string.auth_unknown_error
    }
}
