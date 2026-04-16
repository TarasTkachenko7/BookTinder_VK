package com.practicum.vkproject3.ui.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.practicum.vkproject3.presentation.home.HomeViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.theme.DarkGreen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String, String) -> Unit,
    onNotificationsClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val beigeBackground = colorResource(R.color.beige_background)
    val orangeBrown = colorResource(R.color.orange_brown)
    val iconGray = colorResource(R.color.icon_gray)
    val textBlack = colorResource(R.color.text_black)
    val sheetPeekHeight = dimensionResource(R.dimen.home_sheet_peek_height)
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded, skipHiddenState = true)
    )
    val bookList = state.books
    val pagerState = rememberPagerState(pageCount = { bookList.size })

    var isHeartVisible by remember {mutableStateOf(false)}
    val scale by animateFloatAsState(
        targetValue = if (isHeartVisible) 1.5f else 0f,
        animationSpec =  spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = stringResource(R.string.book_details_favorite_label)
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        containerColor = beigeBackground,
        sheetContainerColor = DarkGreen,
        sheetPeekHeight = sheetPeekHeight,
        sheetDragHandle = {
            Box(
                Modifier
                    .padding(top = 8.dp)
                    .width(60.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(orangeBrown)
            )
        },
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = sheetPeekHeight)
                    .fillMaxHeight(0.85f)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(4.dp))
                Text(text = stringResource(R.string.home_about_book), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    val description = getMockDescription(state.current?.id ?: "default")
                    Text(
                        text = description,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 18.dp, vertical = 10.dp)) {
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = DarkGreen)
                            Spacer(Modifier.height(12.dp))
                            Text(stringResource(R.string.home_loading))
                        }
                    }
                }
                state.error != null -> {
                    Column(
                        Modifier.fillMaxSize(),
                        Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.error_book_load))
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = viewModel::load,
                            colors = ButtonDefaults.buttonColors(containerColor = orangeBrown)
                        ) { Text(stringResource(R.string.home_retry)) }
                    }
                }
                state.isEmpty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.home_no_books)) }
                }
                else -> {
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.End,
                            Alignment.CenterVertically
                        ) {
                            RoundIconButton(
                                Icons.Default.Notifications,
                                iconGray,
                                textBlack,
                                stringResource(R.string.home_notifications_description),
                                onNotificationsClick,
                            )
                        }

                        Spacer(Modifier.height(14.dp))
                        HorizontalPager(
                            state = pagerState
                        ) { bookPage ->
                            val book = bookList[bookPage]
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Card(
                                    Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(500.dp)
                                        .combinedClickable(
                                            onClick = {
                                                val bookId = book.id
                                                val editionId = book.editionId
                                                onBookClick(bookId, editionId)
                                            },
                                            onDoubleClick = {
                                                viewModel.toggleFavorite()
                                                isHeartVisible = true
                                            }
                                        ),
                                    RoundedCornerShape(18.dp),
                                    CardDefaults.cardColors(containerColor = DarkGreen),

                                    ) {
                                    Column(Modifier.fillMaxSize()) {
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            AsyncImage(
                                                model = book.coverUrl,
                                                contentDescription = stringResource(R.string.home_book_cover),
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)),
                                                contentScale = ContentScale.FillBounds
                                            )

                                            if(scale > 0.1f){
                                                Icon(
                                                    imageVector = Icons.Filled.Favorite,
                                                    contentDescription = null,
                                                    tint = orangeBrown,
                                                    modifier = Modifier
                                                        .size(100.dp)
                                                        .scale(scale)
                                                        .align(Alignment.Center)
                                                )
                                            }
                                        }

                                        LaunchedEffect(isHeartVisible) {
                                            if (isHeartVisible) {
                                                delay(500)
                                                isHeartVisible = false
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
                                                overflow = TextOverflow.Ellipsis,
                                                lineHeight = 20.sp
                                            )

                                            Text(
                                                text = book.author,
                                                color = Color.White.copy(alpha = 0.85f),
                                                fontSize = 14.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )

                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp),
                                                Arrangement.SpaceBetween,
                                                Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.home_rating_format, book.rating),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium
                                                )

                                                Text(
                                                    text = genreNameById(book.genreId),
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                                LaunchedEffect(pagerState) {
                                    snapshotFlow { pagerState.currentPage }.collect { page ->
                                        viewModel.onPageChanged(page)
                                    }
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundIconButton(
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        Modifier.size(38.dp).clip(CircleShape).background(backgroundColor.copy(alpha = 0.9f)).clickable { onClick() },
        Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = iconColor.copy(alpha = 0.75f))
    }
}

@Composable
private fun genreNameById(genreId: String): String {
    val context = LocalContext.current
    val resources = context.resources

    val ids = resources.getStringArray(R.array.genre_ids)
    val names = resources.getStringArray(R.array.genre_name_res_ids)

    val index = ids.indexOf(genreId)

    return if (index != -1 && index < names.size) {
        names[index]
    } else {
        stringResource(R.string.genre_null)
    }
}

private val mockDescriptions = listOf(
    "Увлекательная история, полная неожиданных поворотов сюжета и ярких персонажей. Отличный выбор для приятного вечера с книгой.",
    "Глубокий и философский роман, который заставляет задуматься о важных жизненных вопросах и оставляет долгое послевкусие.",
    "Напряженный триллер, где каждая страница держит в напряжении, а финал оказывается совершенно непредсказуемым.",
    "Легкая и романтичная история о любви, дружбе и поиске себя в большом городе. Идеально для поднятия настроения.",
    "Классическое произведение, которое не теряет своей актуальности с годами. Обязательно к прочтению каждому.",
    "Завораживающий мир фэнтези, полный магии, загадок и древних легенд. Погрузитесь в него с головой.",
    "Пронзительная драма о человеческих судьбах, потерях и надежде. Эта книга не оставит вас равнодушным.",
    "Динамичный детектив с запутанным сюжетом и харизматичным главным героем, который шаг за шагом распутывает клубок тайн.",
    "Вдохновляющая биография выдающегося человека, чья жизнь и достижения служат примером для многих.",
    "Сборник трогательных и смешных рассказов о простых людях и их повседневных радостях и горестях."
)

fun getMockDescription(bookId: String): String {
    val index = kotlin.math.abs(bookId.hashCode()) % mockDescriptions.size
    return mockDescriptions[index]
}