// com.practicum.vkproject3.ui.discussions.DiscussionsScreen.kt

package com.practicum.vkproject3.ui.discussions

import ReviewPostCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.practicum.vkproject3.ui.profile.BeigeBackground
import com.practicum.vkproject3.ui.theme.DarkGreen
import androidx.navigation.NavController
import com.practicum.vkproject3.ui.books.SearchBarSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionsScreen(
    navController: NavController,
    viewModel: DiscussionsViewModel = viewModel(),
    onNavigateToFavorites: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Основной контейнер
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F8F4))
            .padding(top = 16.dp)
    ) {
        // 1. СЕКЦИЯ ПОИСКА
        SearchBarSection(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onLikeClick = onNavigateToFavorites
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. ЛЕНТА ПОСТОВ
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3E5A47))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "Рецензии",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                // Фильтруем посты по поисковому запросу
                val filteredPosts = uiState.posts.filter {
                    it.bookTitle.contains(searchQuery, ignoreCase = true) ||
                            it.reviewText.contains(searchQuery, ignoreCase = true)
                }

                items(filteredPosts) { post ->
                    ReviewPostCard(
                        post = post,
                        onClick = { navController.navigate("discussion_chat/${post.id}") }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
