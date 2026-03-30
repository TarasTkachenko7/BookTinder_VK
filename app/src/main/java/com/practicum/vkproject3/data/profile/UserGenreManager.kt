package com.practicum.vkproject3.data.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.tasks.await

class UserGenreManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://booktinder-b0ffb-default-rtdb.europe-west1.firebasedatabase.app")
    private val userGenresRef: DatabaseReference = database.getReference("userGenres")

    private fun getCurrentUserGenresRef(): DatabaseReference? {
        val userId = auth.currentUser?.uid
        return if (userId != null) userGenresRef.child(userId) else null
    }

    suspend fun saveUserGenres(genres: List<String>): Boolean {
        val ref = getCurrentUserGenresRef() ?: return false
        return try {
            ref.setValue(genres).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loadUserGenres(): List<String>? {
        val ref = getCurrentUserGenresRef() ?: return null
        return try {
            val snapshot = ref.get().await()
            if (snapshot.exists()) {
                val type = object : GenericTypeIndicator<List<String>>() {}
                snapshot.getValue(type) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            null
        }
    }
}