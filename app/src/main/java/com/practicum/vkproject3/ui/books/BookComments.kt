package com.practicum.vkproject3.ui.books


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.presentation.books.Comment

@Composable
fun rememberMockComments(): SnapshotStateList<Comment> {
    return remember {
        mutableStateListOf(
            Comment(
                id = 1L,
                authorName = "Петр Петров",
                text = "Это лучшая книга которую я когда-либо читал!",
                replies = mutableStateListOf(
                    Comment(
                        id = 11L,
                        authorName = "Иван Иванов",
                        text = "Я согласен с Петром! Это лучшее, что видело человечество"
                    )
                )
            ),
            Comment(
                id = 2L,
                authorName = "Полиграф Полиграфович",
                text = "Средненькая книга"
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsTabContent() {
    val comments = rememberMockComments()
    var textState by remember { mutableStateOf("") }
    val commentAuthorName = stringResource(R.string.book_details_you)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.book_details_write_comment), color = Color.Gray, fontSize = 16.sp) },
            shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                if (textState.isNotEmpty()) {
                    IconButton(onClick = {
                        comments.add(0, Comment(System.currentTimeMillis(), commentAuthorName, textState))
                        textState = ""
                    }) {
                        Icon(Icons.Default.Send, stringResource(R.string.book_details_send_arrow), tint = MainBrown)
                    }
                }
            },
            colors = outlinedTextFieldColors(
                focusedTextColor = TextBrown,
                unfocusedTextColor = TextBrown,
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        comments.forEach { comment ->
            CommentItem(comment = comment, isTopLevel = true)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CommentItem(comment: Comment, isTopLevel: Boolean) {
    var isLiked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isLiked) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = stringResource(R.string.book_details_favorite_label)
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isTopLevel) 36.dp else 28.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = comment.authorName,
                fontWeight = FontWeight.Bold,
                fontSize = if (isTopLevel) 18.sp else 16.sp,
                modifier = Modifier.weight(1f).alpha(0.85f),
                color = Color.Black
            )
            if (isTopLevel) {
                IconButton(
                    onClick = { isLiked = !isLiked },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.book_details_comment_line),

                        tint = if (isLiked) MainBrown else Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (isTopLevel) 18.dp else 14.dp)
                .height(IntrinsicSize.Min)
        ) {
            if (comment.replies.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(DarkGreen)
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Column(modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)) {
                Text(
                    text = comment.text,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = TextBrown
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        stringResource(R.string.book_details_answer),
                        color = MainBrown,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { }
                    )
                    Text(
                        stringResource(R.string.book_details_complaint),
                        color = MainBrown,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { }
                    )
                }

                comment.replies.forEach { reply ->
                    CommentItem(comment = reply, isTopLevel = false)
                }
            }
        }
    }
}