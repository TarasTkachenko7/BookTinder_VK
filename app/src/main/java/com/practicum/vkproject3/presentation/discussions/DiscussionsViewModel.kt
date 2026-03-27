package com.practicum.vkproject3.ui.discussions

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewPost(
    val id: Int,
    val bookTitle: String,
    val bookAuthor: String,
    val bookRating: Double,
    val membersCount: Int,
    val userNickname: String,
    val reviewText: String,
    val date: String,
    val userAvatarColor: Color = Color(0xFFC26E4B)
)

data class DiscussionsUiState(
    val isLoading: Boolean = true,
    val posts: List<ReviewPost> = emptyList()
)

class DiscussionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DiscussionsUiState())
    val uiState = _uiState.asStateFlow()

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = DiscussionsUiState(isLoading = true)
            delay(1000)
            _uiState.value = DiscussionsUiState(
                isLoading = false,
                posts = listOf(
                    ReviewPost(1, "Преступление и наказание", "Ф. Достоевский", 4.9, 128, "ivan_knigolyub", "Поразительная глубина мысли! Раскольников — это мы все...", "сегодня в 12:40"),
                    ReviewPost(2, "Ведьмак: Последнее желание", "А. Сапковский", 4.8, 540, "geralt_fan", "Перечитываю в десятый раз. Атмосфера просто нереальная.", "вчера в 20:15"),
                    ReviewPost(3, "1984", "Джордж Оруэлл", 4.7, 890, "anti_utopia", "Книга, которая пугает своей актуальностью в любое время.", "2 дня назад")
                )
            )
        }
    }
}
