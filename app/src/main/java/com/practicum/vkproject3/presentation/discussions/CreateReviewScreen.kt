package com.practicum.vkproject3.presentation.discussions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReviewScreen(
    bookId: String?,
    onBackClick: () -> Unit,
    onPublishSuccess: () -> Unit,
    viewModel: DiscussionsViewModel
) {
    val state by viewModel.createReviewState.collectAsState()
    val pickerState by viewModel.bookPickerState.collectAsState()

    var showBookPicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(bookId) {
        if (!bookId.isNullOrBlank() && state.selectedBook?.id != bookId) {
            viewModel.loadBookForReview(bookId)
        }
    }

    if (showBookPicker) {
        ModalBottomSheet(
            onDismissRequest = { showBookPicker = false },
            containerColor = Color.White
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
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Поиск по названию или автору")
                    },
                    shape = RoundedCornerShape(16.dp)
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
                            CircularProgressIndicator()
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
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(
                                onClick = { viewModel.loadBooksForPicker() }
                            ) {
                                Text("Попробовать снова")
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
                            Text("Книги не найдены")
                        }
                    }

                    else -> {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items = filteredBooks, key = { book -> book.id }) { book ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF2C3E34)
                                    ),
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
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Crop
                                        )

                                        Spacer(modifier = Modifier.height(0.dp))

                                        Column(
                                            modifier = Modifier
                                                .padding(start = 12.dp)
                                                .weight(1f)
                                        ) {
                                            Text(
                                                text = book.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.White
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = book.author,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = "Рейтинг",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${book.rating}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (pickerState.isPaginating) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else if (!pickerState.isEndReached && filteredBooks.isNotEmpty() && searchQuery.isBlank()) {
                                item {
                                    LaunchedEffect(Unit) {
                                        viewModel.loadBooksForPicker(isPagination = true)
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
                title = { Text("Новая рецензия") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.resetCreateReviewState()
                            onBackClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
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
                        containerColor = Color(0xFFC56E4A)
                    )
                ) {
                    Text(
                        text = if (state.isPublishing) {
                            "Публикуем..."
                        } else {
                            "Опубликовать"
                        }
                    )
                }
            }
        },
        containerColor = Color(0xFFF9F8F4)
    ) { paddingValues ->
        when {
            state.isBookLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Button(
                            onClick = { showBookPicker = true },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Выбрать книгу")
                        }

                        if (!bookId.isNullOrBlank()) {
                            TextButton(
                                onClick = { viewModel.loadBookForReview(bookId) }
                            ) {
                                Text("Попробовать снова")
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
                                containerColor = Color.White
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Книга не выбрана",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Сначала выбери книгу, для которой хочешь написать рецензию",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { showBookPicker = true },
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text("Выбрать книгу")
                                }
                            }
                        }
                    } else {
                        val book = state.selectedBook!!

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2C3E34)
                            ),
                            onClick = { showBookPicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = book.coverUrl,
                                    contentDescription = book.title,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = book.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = book.author,
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Рейтинг",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp).offset(y = 1.dp)
                                        )
                                        Text(
                                            text = " " + String.format(java.util.Locale.US, "%.1f", book.rating),
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Изменить книгу",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
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
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.reviewText.length} символов",
                        modifier = Modifier.align(Alignment.End),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}