package com.practicum.vkproject3.ui.books

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.domain.model.Book
import com.practicum.vkproject3.presentation.books.BookDetailsUiState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.practicum.vkproject3.presentation.books.BookDetailsViewModel

val DarkGreen = Color(0xFF2C4A42)
val MainBrown = Color(0xFFC77A58)
val BeigeBackground = Color(0xFFF9F8F4)

val TextBrown = Color(0xFF605454)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    bookId: String,
    editionId: String,
    onBack: () -> Unit,
    viewModel: BookDetailsViewModel = koinViewModel(),
    onAddReviewClick: (Book) -> Unit,
) {
    LaunchedEffect(bookId, editionId) {
        viewModel.loadDetails(bookId, editionId)
    }

    val uriHandler = LocalUriHandler.current
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(BeigeBackground),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment= Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.book_details_back_arrow),
                        tint = Color.Black
                    )
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = colorResource(R.color.icon_gray).copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                        .size(38.dp)
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.home_notifications_description),
                        tint = colorResource(R.color.text_black).copy(alpha = 0.75f)
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BeigeBackground)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is BookDetailsUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is BookDetailsUiState.Success -> {
                    val book = state.book
                    val externalUrl = stringResource(R.string.book_details_external_url) + bookId
                    val isFavorite = state.isFavorite
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(440.dp)
                        ){
                            AsyncImage(
                                model = book.imageUrl,
                                contentDescription = stringResource(R.string.book_details_blurred),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                                    .blur(radius = 15.dp)
                                    .alpha(0.85f)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            0.0f to BeigeBackground,
                                            0.15f to BeigeBackground.copy(alpha = 0.2f),
                                            0.4f to Color.Transparent,
                                            0.6f to Color.Transparent,
                                            0.85f to BeigeBackground.copy(alpha = 0.2f),
                                            1.0f to BeigeBackground
                                        )
                                    )
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            0.0f to BeigeBackground,
                                            0.15f to BeigeBackground.copy(alpha = 0.2f),
                                            0.4f to Color.Transparent,
                                            0.6f to Color.Transparent,
                                            0.85f to BeigeBackground.copy(alpha = 0.2f),
                                            1.0f to BeigeBackground
                                        )
                                    )
                            )

                            AsyncImage(
                                model = book.imageUrl,
                                contentDescription = stringResource(R.string.book_details_cover),
                                modifier = Modifier
                                    .height(310.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .align(Alignment.Center),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        BookActionButtons(
                            isFavorite = isFavorite,
                            onFavoriteClick = {viewModel.toggleFavorite(book)},
                            onReadClick = { uriHandler.openUri(externalUrl) },
                            onAddReviewClick = { onAddReviewClick(book) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        val pagerState = rememberPagerState(pageCount = { 2 })
                        val coroutineScope = rememberCoroutineScope()

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = CircleShape,
                            color = DarkGreen
                        ) {
                            TabRow(
                                selectedTabIndex = pagerState.currentPage,
                                containerColor = Color.Transparent,
                                contentColor = Color.White,
                                indicator = { tabPositions ->
                                    if (pagerState.currentPage < tabPositions.size) {
                                        TabRowDefaults.SecondaryIndicator(
                                            Modifier
                                                .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                                .padding(horizontal = 24.dp)
                                                .clip(CircleShape),
                                            color = MainBrown,
                                            height = 3.dp
                                        )
                                    }
                                },
                                divider = {}
                            ) {
                                Tab(
                                    selected = pagerState.currentPage == 0,
                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                                    text = { Text(stringResource(R.string.book_details_about_book), fontWeight = FontWeight.Bold) }
                                )
                                Tab(
                                    selected = pagerState.currentPage == 1,
                                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                                    text = { Text(stringResource(R.string.book_details_comments), fontWeight = FontWeight.Bold) }
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 500.dp)
                                .background(BeigeBackground),
                            verticalAlignment = Alignment.Top
                        ) { page ->
                            if (page == 0) {
                                AboutBookContent(book)
                            } else {
                                CommentsTabContent()
                            }
                        }
                    }
                }

                is BookDetailsUiState.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка: ${state.message}", color = Color.Red)
                        Button(onClick = { viewModel.loadDetails(bookId, editionId) }, colors = ButtonDefaults.buttonColors(containerColor = MainBrown)) {
                            Text(stringResource(R.string.book_details_repeat))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AboutBookContent(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = BeigeBackground
            ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                InfoItemDetail(stringResource(R.string.book_details_genre), book.genre, Modifier.weight(1f))
                InfoItemDetail(stringResource(R.string.book_details_year), book.publishedDate ?: "-", Modifier.weight(1f))
                InfoItemDetail(stringResource(R.string.book_details_pages), book.pages?.toString() ?: "-", Modifier.weight(1f))
                InfoItemDetail(stringResource(R.string.book_details_language), book.languages?.joinToString("/").toString(), Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .border(width = 1.dp, color = DarkGreen, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Icon(
                painter = painterResource(R.drawable.author_ic),
                contentDescription = stringResource(R.string.book_details_author_ic),
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = book.author,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = stringResource(R.string.book_details_description), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkGreen)
        Text(
            text = book.description ?: stringResource(R.string.book_details_description_empty),
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Justify,
            color = TextBrown
        )
    }
}

@Composable
fun InfoItemDetail(
    label: String,
    value: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = DarkGreen,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontFamily = FontFamily.SansSerif,
            color = TextBrown,
            textAlign = TextAlign.Center,
            maxLines = 3,
            lineHeight = 14.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun BookActionButtons(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onReadClick: () -> Unit,
    onAddReviewClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = stringResource(R.string.book_details_favorite_label)
    )

    val heartColor by animateColorAsState(
        targetValue = if (isFavorite) MainBrown else Color.Black,
        label = stringResource(R.string.book_details_heart_label)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onFavoriteClick,
            modifier = Modifier.widthIn(min = 160.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = stringResource(R.string.book_details_favorite_label),
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale),
                tint = heartColor
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.book_details_to_favorites),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }

        Button(
            onClick = onReadClick,
            modifier = Modifier.widthIn(min = 120.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainBrown,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.auto_stories),
                contentDescription = stringResource(R.string.book_details_open_book_ic),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.book_details_read),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }

        Button(
            onClick = onAddReviewClick,
            modifier = Modifier.widthIn(min = 160.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkAdd,
                contentDescription = stringResource(R.string.book_details_write_review),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.book_details_review_text),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
