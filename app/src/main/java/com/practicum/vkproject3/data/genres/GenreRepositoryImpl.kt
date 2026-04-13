package com.practicum.vkproject3.data.genres

import com.google.firebase.database.FirebaseDatabase
import com.practicum.vkproject3.domain.genres.GenreRepository
import com.practicum.vkproject3.domain.model.Genre
import kotlinx.coroutines.tasks.await

class GenreRepositoryImpl : GenreRepository {
    private val databaseRef = FirebaseDatabase.getInstance().getReference("genres")

    override suspend fun getAllGenres(): List<Genre> {
        return try {
            val snapshot = databaseRef.get().await()
            val genresList = mutableListOf<Genre>()

            for (childSnapshot in snapshot.children) {
                val genre = childSnapshot.getValue(Genre::class.java)
                if (genre != null) {
                    genresList.add(genre)
                }
            }
            genresList
        } catch (e: Exception) {
            android.util.Log.e("FIREBASE_ERROR", "Error loading genres: ${e.message}")
            emptyList()
        }
    }
}