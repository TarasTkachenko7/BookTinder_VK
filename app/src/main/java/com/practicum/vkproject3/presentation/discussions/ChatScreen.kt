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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    discussionId: Int,
    onBackClick: () -> Unit,
    // Передаем сюда объект поста (позже будем брать из ViewModel по ID)
) {
    var commentText by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }

    // Имитируем данные (в реальном приложении возьмем из базы)
    val comments = remember { mutableStateListOf("Согласен с автором!", "А мне финал показался затянутым...") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Обсуждение", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                // 1. ШАПКА: Книга и сама рецензия (как на главном, но подробнее)
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        // Повторяем дизайн карточки
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF3E5A47))
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Название книги", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("Автор книги", color = Color.LightGray, fontSize = 14.sp)
                                }
                            }
                        }

                        // Текст рецензии
                        Card(
                            shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9E9E9)),
                            modifier = Modifier.fillMaxWidth().offset(y = (-4).dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Текст самой рецензии, которую мы открыли. Здесь пользователь расписал свои мысли о книге подробно...", fontSize = 15.sp)

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Кнопка Лайка
                                    IconButton(onClick = { isLiked = !isLiked }) {
                                        Icon(
                                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Лайк",
                                            tint = if (isLiked) Color.Red else Color.Gray
                                        )
                                    }
                                    Text("сегодня в 12:40", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Комментарии (${comments.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
                    }
                }

                // 2. СПИСОК КОММЕНТАРИЕВ
                items(comments) { comment ->
                    Card(
                        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(comment, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
                    }
                }
            }

            // 3. ПОЛЕ ВВОДА КОММЕНТАРИЯ
            Surface(shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Оставьте комментарий...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFF5F5DC), unfocusedContainerColor = Color(0xFFF5F5DC))
                    )
                    IconButton(onClick = {
                        if (commentText.isNotBlank()) {
                            comments.add(commentText)
                            commentText = ""
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Отправить", tint = Color(0xFF3E5A47))
                    }
                }
            }
        }
    }
}
