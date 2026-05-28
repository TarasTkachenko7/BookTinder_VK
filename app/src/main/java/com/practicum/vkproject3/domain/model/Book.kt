package com.practicum.vkproject3.domain.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String,
    val imageUrl: String,
    val description: String
)

val mockCatalog = listOf(
    Book("1", "Dictionary of color", "Шон Адамс", 4.9, "Дизайн", "https://covers.openlibrary.org/b/id/14352528-L.jpg", "marabu"),
    Book("2", "Моё прекрасное искупление", "Джейми Макгвайр", 5.0, "Драма", "https://covers.openlibrary.org/b/id/13444469-L.jpg", "barabu"),
    Book("3", "Об интерфейсе", "Алан Купер", 4.8, "Дизайн", "https://covers.openlibrary.org/b/id/14414574-L.jpg", "kakadu"),
    Book("4", "Психбольница", "Алан Купер", 4.7, "Дизайн", "https://covers.openlibrary.org/b/id/10565013-L.jpg", "jimichu")
)

fun mapToDomainBook(
    id: String,
    title: String,
    author: String,
    rating: Double,
    genre: String,
    imageUrl: String,
    description: String
): Book {
    val formattedRating = kotlin.math.round(rating * 10) / 10.0
    
    val translatedGenre = when (genre.trim().lowercase()) {
        "fantasy", "science fiction" -> "Фантастика"
        "detective", "mystery" -> "Детектив"
        "romance" -> "Роман"
        "adventure" -> "Приключения"
        "drama" -> "Драма"
        "fiction" -> "Разное"
        "classic", "classics" -> "Классика"
        "horror" -> "Ужасы"
        "psychology" -> "Психология"
        "science" -> "Научпоп"
        "business" -> "Бизнес"
        else -> genre.trim().replaceFirstChar { it.uppercase() }
    }

    return Book(
        id = id,
        title = title,
        author = author,
        rating = formattedRating,
        genre = translatedGenre,
        imageUrl = imageUrl,
        description = description
    )
}