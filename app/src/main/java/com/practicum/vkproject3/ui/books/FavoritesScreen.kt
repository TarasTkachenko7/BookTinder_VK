package com.practicum.vkproject3.ui.books

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.Book
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.practicum.vkproject3.presentation.books.FavoritesViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onBookClick: (String, String) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val resources = context.resources
    
    val bgColor = Color(0xFFF9F8F4)
    val searchBg = Color(0xFFEBEAE6)
    val orangeAction = Color(0xFFC77A58)

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(resources.getString(R.string.catalog_all)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
    ) {
        // ... (Header Row unchanged)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = resources.getString(R.string.favorites_back)
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(resources.getString(R.string.favorites_search_placeholder), color = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = CircleShape,
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = searchBg,
                    unfocusedContainerColor = searchBg,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(androidx.compose.ui.res.colorResource(R.color.icon_gray).copy(alpha = 0.9f))
                    .clickable { },
                Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = androidx.compose.ui.res.colorResource(R.color.text_black).copy(alpha = 0.75f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val categories = listOf(
            resources.getString(R.string.catalog_all),
            resources.getString(R.string.catalog_fantasy),
            resources.getString(R.string.catalog_drama),
            resources.getString(R.string.catalog_detective),
            resources.getString(R.string.catalog_romance),
            "Дизайн"
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) orangeAction else Color.LightGray.copy(alpha = 0.4f))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = resources.getString(R.string.favorites_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = orangeAction)
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error ?: "Ошибка", color = Color.Red)
            }
        } else {
            val filteredFavorites = remember(searchQuery, selectedCategory, state.books) {
                state.books.filter { book ->
                    val matchesSearch = if (searchQuery.isBlank()) true
                    else book.title.lowercase().contains(searchQuery.lowercase())
                    val matchesCategory = if (selectedCategory == resources.getString(R.string.catalog_all)) true
                    else book.genre.equals(selectedCategory, ignoreCase = true)
                    matchesSearch && matchesCategory
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(filteredFavorites) { book ->
                    FavoriteBookCardItem(book, onBookClick)
                }
                if (filteredFavorites.isEmpty()) {
                    item {
                        Text(
                            "Ничего не найдено",
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteBookCardItem(book: Book, onClick: (String, String) -> Unit) {
    val context = LocalContext.current
    val resources = context.resources
    
    val cardGreen = Color(0xFF2C3E34)
    val btnOrange = Color(0xFFC77A58)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardGreen),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick(book.id, book.edition_id.toString()) }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .fillMaxHeight()
            ) {
                Text(
                    text = book.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Text(
                        text = "${book.rating}",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = book.genre,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = btnOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(36.dp)
                        .width(120.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = resources.getString(R.string.favorites_read_button), 
                        color = Color.White, 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
