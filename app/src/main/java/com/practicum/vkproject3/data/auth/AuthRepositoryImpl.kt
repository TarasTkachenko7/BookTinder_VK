package com.practicum.vkproject3.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.practicum.vkproject3.data.network.api.AuthApi
import com.practicum.vkproject3.data.network.model.AuthRequest
import com.practicum.vkproject3.data.network.model.AuthResponse
import com.practicum.vkproject3.data.network.model.RegisterRequest
import com.practicum.vkproject3.domain.auth.AuthRepository
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val api: AuthApi
) : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val result = auth.createUserWithEmailAndPassword(request.email, request.password).await()
            val user = result.user
            if (user != null) {
                val userMap = mapOf(
                    "uid" to user.uid,
                    "email" to request.email,
                    "name" to "",
                    "genres" to emptyList<String>()
                )
                database.child(user.uid).setValue(userMap)

                user.sendEmailVerification().await()

                Result.success(AuthResponse(token = user.uid, message = "Success"))
            } else {
                Result.failure(Exception("Не удалось получить данные пользователя"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(request: AuthRequest): Result<AuthResponse> {
        return try {
            val result = auth.signInWithEmailAndPassword(request.email, request.password).await()
            val user = result.user
            if (user != null) {
                Result.success(AuthResponse(token = user.uid, message = "Success"))
            } else {
                Result.failure(Exception("Ошибка входа"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isEmailVerified(): Boolean {

        auth.currentUser?.reload()?.await()
        return auth.currentUser?.isEmailVerified ?: false
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Пользователь не авторизован"))
            val uid = user.uid
            

            database.child(uid).removeValue().await()

            user.delete().await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
