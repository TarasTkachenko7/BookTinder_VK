package com.practicum.vkproject3.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.vkproject3.domain.model.Book

@Entity(tableName = "favorite_books")
data class FavoriteBookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String,
    val imageUrl: String,
    val description: String
)

fun FavoriteBookEntity.toDomain() = Book(
    id = id,
    title = title,
    author = author,
    rating = rating,
    genre = genre,
    imageUrl = imageUrl,
    description = description
)

fun Book.toEntity() = FavoriteBookEntity(
    id = id,
    title = title,
    author = author,
    rating = rating,
    genre = genre,
    imageUrl = imageUrl,
    description = description
)
