package com.practicum.vkproject3.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBookDao {
    @Query("SELECT * FROM favorite_books")
    fun observeFavoriteBooks(): Flow<List<FavoriteBookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: FavoriteBookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<FavoriteBookEntity>)

    @Query("DELETE FROM favorite_books WHERE id = :bookId")
    suspend fun deleteBook(bookId: String)

    @Query("DELETE FROM favorite_books")
    suspend fun deleteAll()
}
