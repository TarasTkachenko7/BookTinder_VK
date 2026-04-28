package com.practicum.vkproject3.presentation.discussions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.common.UserAvatar
import com.practicum.vkproject3.ui.theme.BackgroundLight
import com.practicum.vkproject3.ui.theme.Cream
import com.practicum.vkproject3.ui.theme.DarkGreen
import com.practicum.vkproject3.ui.theme.DividerSoft
import com.practicum.vkproject3.ui.theme.ErrorRed
import com.practicum.vkproject3.ui.theme.MainBrown
import com.practicum.vkproject3.ui.theme.SurfaceSoft
import com.practicum.vkproject3.ui.theme.TextPrimary
import com.practicum.vkproject3.ui.theme.TextSecondary
import com.practicum.vkproject3.ui.theme.WarmSand

const val SUPPORT_EMAIL = "support@example.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    discussionId: Int,
    viewModel: DiscussionsViewModel,
    onBackClick: () -> Unit,
    onAddReviewClick: ((String) -> Unit)? = null
) {
    var commentText by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val discussionTitle = stringResource(R.string.discussion_title)
    val discussionBackDescription = stringResource(R.string.favorites_back)
    val discussionNotFound = stringResource(R.string.discussion_not_found)
    val discussionCurrentUserName = stringResource(R.string.discussion_current_user_name)

    val post = viewModel.getPostById(discussionId)
    val comments by viewModel.comments.collectAsState()
    val postComments = comments[discussionId].orEmpty()
    val discussionCommentsCount = stringResource(R.string.discussion_comments_count, postComments.size)

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = discussionTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = discussionBackDescription,
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (post?.bookId != null && onAddReviewClick != null) {
                        var expanded by remember { mutableStateOf(false) }
                        val discussionIdText = discussionId.toString()
                        val overflowMenuContentDescription = stringResource(R.string.discussion_overflow_menu_description)
                        val writeReviewText = stringResource(R.string.discussion_write_review)
                        val copyDiscussionIdText = stringResource(R.string.discussion_copy_id)
                        val reportDiscussionText = stringResource(R.string.discussion_report)
                        val reportSubject = stringResource(R.string.report_discussion_subject, discussionIdText)
                        val reportBody = stringResource(R.string.report_discussion_body, discussionIdText)
                        val overflowButtonSize = dimensionResource(R.dimen.discussion_overflow_button_size)
                        val overflowIconSize = dimensionResource(R.dimen.discussion_overflow_icon_size)

                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier.size(overflowButtonSize)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = overflowMenuContentDescription,
                                tint = MainBrown,
                                modifier = Modifier.size(overflowIconSize)
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = writeReviewText) },
                                onClick = {
                                    expanded = false
                                    onAddReviewClick(post.bookId)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = copyDiscussionIdText) },
                                onClick = {
                                    expanded = false
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(
                                        ClipData.newPlainText("discussionId", discussionIdText)
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = reportDiscussionText) },
                                onClick = {
                                    expanded = false
                                    val intent = Intent(
                                        Intent.ACTION_SENDTO,
                                        Uri.parse(
                                            "mailto:$SUPPORT_EMAIL" +
                                                    "?subject=${Uri.encode(reportSubject)}" +
                                                    "&body=${Uri.encode(reportBody)}"
                                        )
                                    )

                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (post == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = discussionNotFound,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    horizontal = dimensionResource(R.dimen.discussion_spacing_16),
                    vertical = dimensionResource(R.dimen.discussion_spacing_12)
                )
            ) {
                item {
                    BookHeaderCard(post = post)

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_14)))

                    ReviewCard(
                        post = post,
                        isLiked = isLiked,
                        onLikeClick = { isLiked = !isLiked }
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_24)))

                    Text(
                        text = discussionCommentsCount,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.discussion_spacing_10)),
                        color = DividerSoft
                    )
                }

                items(postComments) { comment ->
                    CommentCard(comment = comment)
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_8)))
                }

                item {
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_8)))
                }
            }

            CommentInputBar(
                value = commentText,
                onValueChange = { commentText = it },
                onSendClick = {
                    val trimmed = commentText.trim()
                    if (trimmed.isNotEmpty()) {
                        viewModel.addComment(discussionId, trimmed, discussionCurrentUserName)
                        commentText = ""
                    }
                }
            )
        }
    }
}

@Composable
private fun BookHeaderCard(post: ReviewPost) {
    val discussionRatingText = stringResource(R.string.discussion_rating_text, post.bookRating.toString())

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.discussion_card_corner_radius_8)),
        colors = CardDefaults.cardColors(containerColor = DarkGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.discussion_elevation_2))
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(R.dimen.discussion_spacing_16)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.bookCoverUrl,
                contentDescription = post.bookTitle,
                modifier = Modifier
                    .size(
                        width = dimensionResource(R.dimen.discussion_book_cover_width_large),
                        height = dimensionResource(R.dimen.discussion_book_cover_height_large)
                    )
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.discussion_card_corner_radius_8)))
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.discussion_spacing_14)))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.discussion_spacing_6))
            ) {
                Text(
                    text = post.bookTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Cream
                )

                Text(
                    text = post.bookAuthor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Cream.copy(alpha = 0.82f)
                )

                Surface(
                    shape = RoundedCornerShape(dimensionResource(R.dimen.discussion_rating_pill_corner_radius)),
                    color = MainBrown.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = discussionRatingText,
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(R.dimen.discussion_spacing_10),
                            vertical = dimensionResource(R.dimen.discussion_spacing_6)
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = Cream
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(
    post: ReviewPost,
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    val discussionMembersCount = stringResource(R.string.discussion_members_count, post.membersCount)
    val discussionLikeDescription = stringResource(R.string.discussion_like_description)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = dimensionResource(R.dimen.discussion_offset_minus_2)),
        shape = RoundedCornerShape(dimensionResource(R.dimen.discussion_card_corner_radius_8)),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.discussion_elevation_1))
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.discussion_spacing_18))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    nickname = post.userNickname,
                    avatarUrl = post.userAvatarUrl,
                    modifier = Modifier.size(dimensionResource(R.dimen.discussion_avatar_size_medium))
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.discussion_spacing_10)))

                Column {
                    Text(
                        text = post.userNickname,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = discussionMembersCount,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_16)))

            Text(
                text = post.reviewText,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_14)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = discussionLikeDescription,
                        tint = if (isLiked) ErrorRed else TextSecondary
                    )
                }

                Text(
                    text = post.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun CommentCard(comment: ReviewComment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensionResource(R.dimen.discussion_card_corner_radius_8)),
        colors = CardDefaults.cardColors(containerColor = WarmSand.copy(alpha = 0.24f)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.discussion_elevation_0))
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.discussion_comment_card_padding_horizontal),
                vertical = dimensionResource(R.dimen.discussion_spacing_12)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(
                    nickname = comment.authorNickname,
                    avatarUrl = comment.authorAvatarUrl,
                    modifier = Modifier.size(dimensionResource(R.dimen.discussion_avatar_size_small))
                )

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.discussion_spacing_10)))

                Column {
                    Text(
                        text = comment.authorNickname,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = comment.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.discussion_spacing_8)))

            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val discussionCommentPlaceholder = stringResource(R.string.discussion_comment_placeholder)
    val discussionSendDescription = stringResource(R.string.discussion_send_description)

    Surface(
        color = SurfaceSoft,
        tonalElevation = dimensionResource(R.dimen.discussion_elevation_1),
        shadowElevation = dimensionResource(R.dimen.discussion_elevation_6)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.discussion_spacing_16),
                    vertical = dimensionResource(R.dimen.discussion_spacing_14)
                )
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = discussionCommentPlaceholder,
                        color = TextSecondary
                    )
                },
                shape = RoundedCornerShape(dimensionResource(R.dimen.discussion_card_corner_radius_8)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Cream,
                    unfocusedContainerColor = Cream,
                    disabledContainerColor = Cream,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MainBrown,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.discussion_spacing_10)))

            Surface(
                onClick = onSendClick,
                shape = CircleShape,
                color = DarkGreen,
                shadowElevation = dimensionResource(R.dimen.discussion_elevation_0),
                tonalElevation = dimensionResource(R.dimen.discussion_elevation_0)
            ) {
                Box(
                    modifier = Modifier.size(dimensionResource(R.dimen.discussion_send_button_size)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = discussionSendDescription,
                        tint = Cream
                    )
                }
            }
        }
    }
}
