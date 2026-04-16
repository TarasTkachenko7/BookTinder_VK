package com.practicum.vkproject3.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String,
    val imageUrl: String,
    val edition_id: String? = null,
    val description: String? = null,
    val pages: Int? = null,
    val publishedDate: String? = null,
    val languages: List<String>? = null
)

val mockCatalog = listOf(
    Book("1", "Словарь цвета", "Шон Адамс", 4.9, "Дизайн", "https://covers.openlibrary.org/b/id/14352528-L.jpg"),
    Book("2", "Моё прекрасное искупление", "Джейми Макгвайр", 5.0, "Драма", "https://covers.openlibrary.org/b/id/13444469-L.jpg"),
    Book("3", "Об интерфейсе", "Алан Купер", 4.8, "Дизайн", "https://covers.openlibrary.org/b/id/14414574-L.jpg"),
    Book("4", "Психбольница", "Алан Купер", 4.7, "Дизайн", "https://covers.openlibrary.org/b/id/10565013-L.jpg")
)