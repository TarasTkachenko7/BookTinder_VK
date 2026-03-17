package com.practicum.vkproject3.ui.books

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.practicum.vkproject3.domain.model.mockCatalog

private val CardDarkGreen = Color(0xFF2C3E34)
private val AccentOrange = Color(0xFFC77A58)

@Composable
fun CatalogScreen(
    onNavigateToFavorites: () -> Unit,
    onBookClick: (String) -> Unit
) {
    val context = LocalContext.current
    val resources = context.resources
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(resources.getString(R.string.catalog_all)) }
    
    val categories = listOf(
        resources.getString(R.string.catalog_all),
        resources.getString(R.string.catalog_fantasy),
        resources.getString(R.string.catalog_drama),
        resources.getString(R.string.catalog_detective),
        resources.getString(R.string.catalog_romance),
        resources.getString(R.string.catalog_horror),
        resources.getString(R.string.catalog_adventure),
        "Дизайн"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F8F4))
            .padding(top = 16.dp)
    ) {
        SearchBarSection(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onLikeClick = onNavigateToFavorites
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { selectedCategory = category },
                    label = { Text(category) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        val filteredCatalog = remember(searchQuery, selectedCategory) {
            mockCatalog.filter { book ->
                val matchesSearch = if (searchQuery.isBlank()) true
                    else book.title.lowercase().contains(searchQuery.lowercase())
                val matchesCategory = if (selectedCategory == resources.getString(R.string.catalog_all)) true
                    else book.genre.equals(selectedCategory, ignoreCase = true)
                matchesSearch && matchesCategory
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = resources.getString(R.string.catalog_recommendations), 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Bold
                )
            }

            items(filteredCatalog) { book ->
                CatalogBookCard(book, onClick = { onBookClick(book.id) })
            }

            if (filteredCatalog.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
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

@Composable
fun CatalogBookCard(book: Book, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardDarkGreen)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(book.author, color = Color.White.copy(0.7f), fontSize = 12.sp, maxLines = 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star, null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Text(" ${book.rating}", color = Color.White, fontSize = 11.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(book.genre, color = Color.White.copy(0.5f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SearchBarSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLikeClick: () -> Unit
) {
    val context = LocalContext.current
    val resources = context.resources
    
    Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text(resources.getString(R.string.catalog_search_placeholder)) },
            modifier = Modifier.weight(1f).height(50.dp),
            shape = CircleShape,
            leadingIcon = { Icon(Icons.Default.Search, null) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEBEBEB),
                unfocusedContainerColor = Color(0xFFEBEBEB),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(androidx.compose.ui.res.colorResource(R.color.icon_gray).copy(alpha = 0.9f))
                .clickable { onLikeClick() },
            Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = androidx.compose.ui.res.colorResource(R.color.text_black).copy(alpha = 0.75f)
            )
        }
        Spacer(Modifier.width(10.dp))
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
}
