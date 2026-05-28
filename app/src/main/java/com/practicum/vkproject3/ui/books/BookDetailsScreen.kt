package com.practicum.vkproject3.ui.books

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.Book
import com.practicum.vkproject3.ui.theme.BeigeBackground
import com.practicum.vkproject3.presentation.books.BookDetailsViewModel
import org.koin.androidx.compose.koinViewModel

val DarkGreen = Color(0xFF2C4A42)
val MainBrown = Color(0xFFC77A58)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    bookId: String,
    onBack: () -> Unit,
    onAddReviewClick: (Book) -> Unit,
    viewModel: BookDetailsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val resources = context.resources

    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize().background(BeigeBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = DarkGreen)
        }
        return
    }

    val book = state.book
    if (book == null) {
        Box(Modifier.fillMaxSize().background(BeigeBackground), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Книга не найдена", fontSize = 18.sp, color = Color.Black)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = MainBrown)) {
                    Text("Вернуться назад", color = Color.White)
                }
            }
        }
        return
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BeigeBackground)
            )
        },
        sheetPeekHeight = 60.dp,
        sheetContainerColor = DarkGreen,
        sheetContentColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetDragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MainBrown)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "О книге",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${book.author} - известный писатель...",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = book.description,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
            }
        },
        containerColor = BeigeBackground
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(500.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkGreen)
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            if (book.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = book.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color.Gray)
                                        .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp))
                                )
                            }
                        }

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = book.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 2,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                lineHeight = 20.sp
                            )

                            Text(
                                text = book.author,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format(java.util.Locale.US, "%.1f", book.rating),
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = book.genre,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onAddReviewClick(book) },
                    colors = ButtonDefaults.buttonColors(containerColor = MainBrown),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Написать рецензию",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}