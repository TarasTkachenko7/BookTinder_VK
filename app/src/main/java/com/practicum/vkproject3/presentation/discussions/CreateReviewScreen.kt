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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Выберите книгу",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Поиск по названию или автору")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainBrown,
                        unfocusedBorderColor = DividerSoft
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                when {
                    pickerState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = DarkGreen)
                        }
                    }

                    pickerState.error != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = pickerState.error ?: "Ошибка",
                                color = ErrorRed
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(
                                onClick = { viewModel.loadBooksForPicker() }
                            ) {
                                Text("Попробовать снова", color = MainBrown)
                            }
                        }
                    }

                    filteredBooks.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Книги не найдены", color = TextSecondary)
                        }
                    }

                    else -> {
                        LazyColumn {
                            items(filteredBooks) { book ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Cream
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    onClick = {
                                        viewModel.selectBookForReview(book)
                                        showBookPicker = false
                                        searchQuery = ""
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = book.coverUrl,
                                            contentDescription = book.title,
                                            modifier = Modifier
                                                .width(50.dp)
                                                .height(75.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.FillBounds
                                        )

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = book.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = DarkGreen
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = book.author,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = "⭐ ${book.rating}",
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
                        "Новая рецензия",
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
                            contentDescription = "Назад",
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
                shadowElevation = 8.dp,
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
                        .padding(16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainBrown,
                        disabledContainerColor = DividerSoft
                    )
                ) {
                    Text(
                        text = if (state.isPublishing) {
                            "Публикуем..."
                        } else {
                            "Опубликовать"
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

            state.error != null && state.selectedBook == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = state.error ?: "Ошибка",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ErrorRed
                        )

                        Button(
                            onClick = { showBookPicker = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainBrown
                            )
                        ) {
                            Text("Выбрать книгу", color = Cream)
                        }

                        if (!bookId.isNullOrBlank()) {
                            TextButton(
                                onClick = { viewModel.loadBookForReview(bookId) }
                            ) {
                                Text("Попробовать снова", color = MainBrown)
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
                        .padding(16.dp)
                ) {
                    if (state.selectedBook == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = SurfaceSoft
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Книга не выбрана",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Сначала выбери книгу, для которой хочешь написать рецензию",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { showBookPicker = true },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MainBrown
                                    )
                                ) {
                                    Text("Выбрать книгу", color = Cream)
                                }
                            }
                        }
                    } else {
                        val book = state.selectedBook!!

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = DarkGreen
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = book.coverUrl,
                                    contentDescription = book.title,
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.FillBounds
                                )

                                Spacer(modifier = Modifier.width(12.dp))

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
                                        text = "⭐ ${book.rating}",
                                        color = WarmSand,
                                        fontSize = 11.sp
                                    )
                                }

                                TextButton(
                                    onClick = { showBookPicker = true }
                                ) {
                                    Text(
                                        "Изменить",
                                        color = WarmSand
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = state.reviewText,
                        onValueChange = viewModel::onReviewTextChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        placeholder = {
                            Text("Напишите рецензию...")
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainBrown,
                            unfocusedBorderColor = DividerSoft,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedPlaceholderColor = TextSecondary,
                            unfocusedPlaceholderColor = TextSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.reviewText.length} символов",
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}