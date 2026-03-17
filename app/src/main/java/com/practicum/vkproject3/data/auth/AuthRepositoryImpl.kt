package com.practicum.vkproject3.data.auth

import com.practicum.vkproject3.data.network.api.AuthApi
import com.practicum.vkproject3.data.network.model.AuthRequest
import com.practicum.vkproject3.data.network.model.AuthResponse
import com.practicum.vkproject3.data.network.model.RegisterRequest
import com.practicum.vkproject3.domain.auth.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl(
    private val api: AuthApi
) : AuthRepository {
    private val useMockBackend = true

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        if (useMockBackend) {
            delay(1500)
            return if (request.email == "SparksVk@mail.ru") {
                Result.failure(Exception("Аккаунт уже зарегистрирован"))
            } else {
                Result.success(AuthResponse(
                    token = "mock_token_register_12345",
                    message = "Success")
                )
            }
        }

        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(request: AuthRequest): Result<AuthResponse> {
         if (useMockBackend) {
            delay(1500)
            return if (request.email == "test@test.com" && request.password == "password123") {
                Result.success(AuthResponse(
                    token = "mock_token_login_98765",
                    message = "Success")
                )
            } else if (request.email == "test@test.com") {
                Result.failure(Exception("Неверный пароль"))
            } else {
                Result.failure(Exception("Почта указана неверно"))
            }
        }

        return try {
            val response = api.login(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
