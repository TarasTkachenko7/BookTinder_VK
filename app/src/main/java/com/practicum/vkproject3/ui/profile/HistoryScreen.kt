package com.practicum.vkproject3.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.profile.BeigeBackground
import com.practicum.vkproject3.ui.theme.DarkGreen

data class HistoryBook(
    val title: String,
    val author: String,
    val rating: Double,
    val genre: String,
    val coverUrl: String
)

val mockHistoryList = listOf(
    HistoryBook("Словарь цвета для дизайнеров", "Шон Адамс", 4.9, "Дизайн", "https://covers.openlibrary.org/b/id/12547191-L.jpg"),
    HistoryBook("Моё прекрасное искупление", "Джейми Макгвайр", 5.0, "Драма", "https://covers.openlibrary.org/b/id/8259443-L.jpg"),
    HistoryBook("Морана и тень. Плетущая", "Лия Арден", 5.0, "Фэнтези", "https://covers.openlibrary.org/b/id/10603765-L.jpg"),
    HistoryBook("Девушка с татуировкой дракона", "Стиг Ларссон", 4.8, "Детектив", "https://covers.openlibrary.org/b/id/10524412-L.jpg")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Black)
                    }
                    BadgedBox(
                        badge = { Badge(containerColor = Color.Red) { } },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BeigeBackground)
            )
        },
        containerColor = BeigeBackground
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = stringResource(R.string.profile_history),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(mockHistoryList) { book ->
                    HistoryItemCard(book)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(book: HistoryBook) {
    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGreen)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = null,
                modifier = Modifier.width(100.dp).fillMaxHeight().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(
                    book.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(book.author, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star, null,
                            tint = if (index < book.rating.toInt()) Color.White else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        "${book.rating}",
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Text(book.genre, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC26E4B)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End).height(36.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(stringResource(R.string.favorites_read_button), color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}