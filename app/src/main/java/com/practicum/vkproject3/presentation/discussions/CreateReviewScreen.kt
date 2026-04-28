package com.practicum.vkproject3.presentation.discussions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReviewScreen(
    bookId: String?,
    viewModel: DiscussionsViewModel,
    onBackClick: () -> Unit,
    onPublishSuccess: () -> Unit
) {
    val state by viewModel.createReviewState.collectAsState()
    val pickerState by viewModel.bookPickerState.collectAsState()
    val pickerTitle = stringResource(R.string.create_review_book_picker_title)
    val pickerSearchPlaceholder = stringResource(R.string.create_review_book_picker_search_placeholder)
    val genericError = stringResource(R.string.error_generic)
    val retryText = stringResource(R.string.home_retry)
    val booksNotFoundText = stringResource(R.string.create_review_books_not_found)
    val reviewTitle = stringResource(R.string.create_review_title)
    val backDescription = stringResource(R.string.favorites_back)
    val publishingText = stringResource(R.string.create_review_publishing)
    val publishText = stringResource(R.string.create_review_publish)
    val chooseBookText = stringResource(R.string.create_review_choose_book)
    val noBookSelectedText = stringResource(R.string.create_review_no_book_selected)
    val noBookSelectedDescription = stringResource(R.string.create_review_no_book_selected_description)
    val changeBookText = stringResource(R.string.create_review_change_book)
    val reviewPlaceholder = stringResource(R.string.create_review_placeholder)
    val reviewCharactersCount = stringResource(R.string.create_review_character_count, state.reviewText.length)

    var showBookPicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(bookId) {
        if (bookId.isNullOrBlank()) {
            viewModel.clearSelectedBookForReview()
        } else if (state.selectedBook?.id != bookId && !state.isBookLoading) {
            viewModel.loadBookForReview(bookId)
        }
    }

    if (showBookPicker) {
        ModalBottomSheet(
            onDismissRequest = { showBookPicker = false },
            containerColor = SurfaceSoft
        ) {
            LaunchedEffect(Unit) {
                if (pickerState.books.isEmpty() && !pickerState.isLoading) {
                    viewModel.loadBooksForPicker()
                }
            }

            val filteredBooks = viewModel.searchBooksForPicker(searchQuery)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.create_review_spacing_16))
                    .padding(bottom = dimensionResource(R.dimen.create_review_spacing_16))
            ) {
                Text(
                    text = pickerTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_12)))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(pickerSearchPlaceholder)
                    },
                    shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_16)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainBrown,
                        unfocusedBorderColor = DividerSoft
                    )
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_12)))

                when {
                    pickerState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(R.dimen.create_review_spacing_24)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DarkGreen)
                        }
                    }

                    pickerState.errorResId != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dimensionResource(R.dimen.create_review_spacing_12)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(pickerState.errorResId ?: R.string.error_generic),
                                color = ErrorRed
                            )

                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_8)))

                            TextButton(
                                onClick = { viewModel.loadBooksForPicker() }
                            ) {
                                Text(retryText, color = MainBrown)
                            }
                        }
                    }

                    filteredBooks.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(R.dimen.create_review_spacing_24)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(booksNotFoundText, color = TextSecondary)
                        }
                    }

                    else -> {
                        LazyColumn {
                            items(filteredBooks) { book ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = dimensionResource(R.dimen.create_review_spacing_6)),
                                    shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_12)),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Cream
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = dimensionResource(R.dimen.create_review_elevation_1)
                                    ),
                                    onClick = {
                                        viewModel.selectBookForReview(book)
                                        showBookPicker = false
                                        searchQuery = ""
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(dimensionResource(R.dimen.create_review_spacing_12)),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = book.coverUrl,
                                            contentDescription = book.title,
                                            modifier = Modifier
                                                .width(dimensionResource(R.dimen.create_review_book_cover_width_small))
                                                .height(dimensionResource(R.dimen.create_review_book_cover_height_small))
                                                .clip(RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_8))),
                                            contentScale = ContentScale.FillBounds
                                        )

                                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.create_review_spacing_12)))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = book.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = DarkGreen
                                            )

                                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_4)))

                                            Text(
                                                text = book.author,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )

                                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_4)))

                                            Text(
                                                text = stringResource(R.string.discussion_rating_text, book.rating.toString()),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MainBrown
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        reviewTitle,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.resetCreateReviewState()
                            onBackClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = backDescription,
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = dimensionResource(R.dimen.create_review_elevation_8),
                color = BackgroundLight
            ) {
                Button(
                    onClick = {
                        viewModel.publishReview {
                            onPublishSuccess()
                        }
                    },
                    enabled = state.canPublish && !state.isPublishing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.create_review_spacing_16))
                        .height(dimensionResource(R.dimen.create_review_button_height)),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_16)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainBrown,
                        disabledContainerColor = DividerSoft
                    )
                ) {
                    Text(
                        text = if (state.isPublishing) {
                            publishingText
                        } else {
                            publishText
                        },
                        color = Cream
                    )
                }
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        when {
            state.isBookLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkGreen)
                }
            }

            state.errorResId != null && state.selectedBook == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(dimensionResource(R.dimen.create_review_spacing_24)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.create_review_spacing_12))
                    ) {
                        Text(
                            text = stringResource(state.errorResId ?: R.string.error_generic),
                            style = MaterialTheme.typography.bodyLarge,
                            color = ErrorRed
                        )

                        Button(
                            onClick = { showBookPicker = true },
                            shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_16)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainBrown
                            )
                        ) {
                            Text(chooseBookText, color = Cream)
                        }

                        if (!bookId.isNullOrBlank()) {
                            TextButton(
                                onClick = { viewModel.loadBookForReview(bookId) }
                            ) {
                                Text(retryText, color = MainBrown)
                            }
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(dimensionResource(R.dimen.create_review_spacing_16))
                ) {
                    if (state.selectedBook == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_16)),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceSoft
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = dimensionResource(R.dimen.create_review_elevation_2)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(dimensionResource(R.dimen.create_review_spacing_24)),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = noBookSelectedText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_8)))

                                Text(
                                    text = noBookSelectedDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_16)))

                                Button(
                                    onClick = { showBookPicker = true },
                                    shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_16)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MainBrown
                                    )
                                ) {
                                    Text(chooseBookText, color = Cream)
                                }
                            }
                        }
                    } else {
                        val book = state.selectedBook!!

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_12)),
                            colors = CardDefaults.cardColors(
                                containerColor = DarkGreen
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = dimensionResource(R.dimen.create_review_elevation_2)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(dimensionResource(R.dimen.create_review_spacing_12)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = book.coverUrl,
                                    contentDescription = book.title,
                                    modifier = Modifier
                                        .width(dimensionResource(R.dimen.create_review_book_cover_width_medium))
                                        .height(dimensionResource(R.dimen.create_review_book_cover_height_medium))
                                        .clip(RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_8))),
                                    contentScale = ContentScale.FillBounds
                                )

                                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.create_review_spacing_12)))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = book.title,
                                        color = Cream,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = book.author,
                                        color = Cream.copy(alpha = 0.82f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.discussion_rating_text, book.rating.toString()),
                                        color = WarmSand,
                                        fontSize = 11.sp
                                    )
                                }

                                TextButton(
                                    onClick = { showBookPicker = true }
                                ) {
                                    Text(
                                        changeBookText,
                                        color = WarmSand
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_20)))

                    OutlinedTextField(
                        value = state.reviewText,
                        onValueChange = viewModel::onReviewTextChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = {
                            Text(reviewPlaceholder)
                        },
                        shape = RoundedCornerShape(dimensionResource(R.dimen.create_review_corner_radius_16)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainBrown,
                            unfocusedBorderColor = DividerSoft,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedPlaceholderColor = TextSecondary,
                            unfocusedPlaceholderColor = TextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.create_review_spacing_8)))

                    Text(
                        text = reviewCharactersCount,
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
