package com.practicum.vkproject3.presentation.discussions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    discussionId: Int,
    viewModel: DiscussionsViewModel,
    onBackClick: () -> Unit,
    onAddReviewClick: (() -> Unit)? = null
) {
    var commentText by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }

    val post = viewModel.getPostById(discussionId)
    val comments by viewModel.comments.collectAsState()
    val postComments = comments[discussionId].orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.discussion_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.placeholder_back))
                    }
                },
                actions = {
                }
            )
        }
    ) { padding ->
        if (post == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.discussion_no_review_found))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3E5A47))
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                AsyncImage(
                                    model = post.bookCoverUrl,
                                    contentDescription = post.bookTitle,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        post.bookTitle,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        post.bookAuthor,
                                        color = Color.LightGray,
                                        fontSize = 14.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = stringResource(R.string.discussion_rating),
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = stringResource(R.string.home_rating_format, post.bookRating),
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9E9E9)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-4).dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = post.reviewText,
                                    fontSize = 15.sp
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                            IconButton(onClick = { isLiked = !isLiked }) {
                                                Icon(
                                                    imageVector = if (isLiked) {
                                                        Icons.Default.Favorite
                                                    } else {
                                                        Icons.Default.FavoriteBorder
                                                    },
                                            contentDescription = stringResource(R.string.discussion_like),
                                                    tint = if (isLiked) Color(0xFFC56E4A) else Color.Gray
                                                )
                                            }

                                    Text(
                                        post.date,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            stringResource(R.string.discussion_comments_title, postComments.size),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.LightGray
                        )
                    }
                }

                items(postComments) { comment ->
                    Card(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(comment.text, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(comment.date, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.discussion_comment_placeholder)) },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addComment(discussionId, commentText)
                                commentText = ""
                            }
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.discussion_send),
                            tint = Color(0xFF3E5A47)
                        )
                    }
                }
            }
        }
    }
}
