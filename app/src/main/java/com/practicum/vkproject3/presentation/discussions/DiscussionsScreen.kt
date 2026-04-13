package com.practicum.vkproject3.presentation.discussions
import ReviewPostCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionsScreen(
    navController: NavController,
    viewModel: DiscussionsViewModel,
    onNavigateToFavorites: () -> Unit = {},
    onAddReviewClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddReviewClick,
                containerColor = Color(0xFFC77A58)
            ) {
                Text("+")
            }
        },
        containerColor = Color(0xFFF9F8F4)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F8F4))
                .padding(innerPadding)
                .padding(top = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

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
}