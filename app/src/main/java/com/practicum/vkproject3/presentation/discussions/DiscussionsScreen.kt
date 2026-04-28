// com.practicum.vkproject3.ui.discussions.DiscussionsScreen.kt

package com.practicum.vkproject3.presentation.discussions

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.profile.BeigeBackground
import com.practicum.vkproject3.ui.theme.DarkGreen
import androidx.navigation.NavController
import com.practicum.vkproject3.ui.books.SearchBarSection

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
    val addReviewFabText = stringResource(R.string.discussion_add_review_fab_text)
    val reviewsTitle = stringResource(R.string.discussion_reviews_title)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddReviewClick,
                containerColor = Color(0xFFC77A58)
            ) {
                Text(addReviewFabText)
            }
        },
        containerColor = Color(0xFFF9F8F4)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F8F4))
                .padding(innerPadding)
                .padding(top = dimensionResource(R.dimen.discussion_spacing_16))
        ) {
            SearchBarSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onLikeClick = onNavigateToFavorites
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_8)))

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF3E5A47))
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = dimensionResource(R.dimen.discussion_spacing_16))
                ) {
                    item {
                        Text(
                            text = reviewsTitle,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.discussion_spacing_12))
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

                    item { Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_80))) }
                }
            }
        }
    }
}
