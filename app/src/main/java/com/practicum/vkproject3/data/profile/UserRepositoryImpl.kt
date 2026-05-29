package com.practicum.vkproject3.data.profile

import android.content.Context
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.UserProfile
import com.practicum.vkproject3.domain.profile.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose

class UserRepositoryImpl(
    private val context: Context,
    private val genreManager: UserGenreManager
) : UserRepository {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override suspend fun getProfile(): UserProfile {
        delay(500)

        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            try {
                val snapshot = com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("users").child(uid).get().await()
                
                val userRecord = snapshot.getValue(com.practicum.vkproject3.data.model.User::class.java)
                if (userRecord != null) {
                    prefs.edit().apply {
                        putString("user_name", userRecord.name)
                        putString("user_avatar", userRecord.avatarUrl)
                        putStringSet("user_genres", userRecord.favoriteGenres.toSet())
                        apply()
                    }
                    UserSession.userName = userRecord.name
                    UserSession.userAvatarUrl = userRecord.avatarUrl
                    UserSession.selectedGenres = userRecord.favoriteGenres.toSet()
                }
            } catch (e: Exception) {
            }
        }

        val firebaseGenres = genreManager.loadUserGenres()
        val finalGenres = if (firebaseGenres != null && firebaseGenres.isNotEmpty()) {
            prefs.edit().putStringSet("user_genres", firebaseGenres.toSet()).apply()
            UserSession.selectedGenres = firebaseGenres.toSet()
            firebaseGenres.toSet()
        } else {
            prefs.getStringSet("user_genres", UserSession.selectedGenres) ?: emptySet()
        }

        val name = prefs.getString("user_name", UserSession.userName ?: context.getString(R.string.user_default_name)) ?: ""
        val avatarUrl = prefs.getString("user_avatar", UserSession.userAvatarUrl)

        return UserProfile(
            name = name,
            email = context.getString(R.string.user_default_email),
            phone = context.getString(R.string.user_default_phone),
            avatarUrl = avatarUrl,
            favoriteGenres = finalGenres.toList()
        )
    }

    override suspend fun updateProfile(name: String, genres: List<String>, avatarUrl: String?): Boolean {
        delay(500)

        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            try {
                val updates = mutableMapOf<String, Any>(
                    "name" to name,
                    "favoriteGenres" to genres
                )
                if (avatarUrl != null) {
                    updates["avatarUrl"] = avatarUrl
                }
                
                com.google.firebase.database.FirebaseDatabase.getInstance()
                    .getReference("users").child(uid)
                    .updateChildren(updates).await()
            } catch (e: Exception) {
                return false
            }
        }

        genreManager.saveUserGenres(genres)

        prefs.edit().apply {
            putString("user_name", name)
            putString("user_avatar", avatarUrl)
            putStringSet("user_genres", genres.toSet())
            apply()
        }

        UserSession.userName = name
        UserSession.selectedGenres = genres.toSet()
        UserSession.userAvatarUrl = avatarUrl
        return true
    }

    override suspend fun restorePassword(email: String): Boolean {
        delay(1500)
        return email.contains("@")
    }

    override fun observeUserGenres(): kotlinx.coroutines.flow.Flow<List<String>> = kotlinx.coroutines.flow.callbackFlow {
        val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = com.google.firebase.database.FirebaseDatabase.getInstance()
            .getReference("users").child(uid).child("favoriteGenres")

        val listener = object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val type = object : com.google.firebase.database.GenericTypeIndicator<List<String>>() {}
                val genres = snapshot.getValue(type) ?: emptyList()
                trySend(genres)
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
