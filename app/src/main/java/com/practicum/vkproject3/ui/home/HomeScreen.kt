package com.practicum.vkproject3.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.practicum.vkproject3.presentation.home.HomeViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onInfoClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showOnboarding by remember { mutableStateOf(false) }
    val beigeBackground = colorResource(R.color.beige_background)
    val orangeBrown = colorResource(R.color.orange_brown)
    val iconGray = colorResource(R.color.icon_gray)
    val textBlack = colorResource(R.color.text_black)
    val sheetPeekHeight = dimensionResource(R.dimen.home_sheet_peek_height)
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded, skipHiddenState = true)
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
                    val book = state.current!!
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.SpaceBetween,
                            Alignment.CenterVertically
                        ) {
                            RoundIconButton(
                                Icons.Default.Info,
                                iconGray,
                                textBlack,
                                stringResource(R.string.home_info_description),
                                onClick = {
                                    onInfoClick()
                                    showOnboarding = true
                                }
                            )
                            Row {
                                RoundIconButton(
                                    icon = Icons.Default.FavoriteBorder,
                                    backgroundColor = iconGray,
                                    iconColor = textBlack,
                                    contentDescription = stringResource(R.string.home_favorites_description),
                                    onClick = onFavoritesClick
                                )
                                Spacer(Modifier.width(10.dp))
                                RoundIconButton(
                                    Icons.Default.Notifications,
                                    iconGray,
                                    textBlack,
                                    stringResource(R.string.home_notifications_description),
                                    onNotificationsClick
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))

                        Card(
                            Modifier
                                .fillMaxWidth(0.75f)
                                .height(500.dp),
                            RoundedCornerShape(18.dp),
                            CardDefaults.cardColors(containerColor = DarkGreen)
                        ) {
                            Column(Modifier.fillMaxSize()) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                ) {
                                    AsyncImage(
                                        model = book.coverUrl,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(bottomStart = 14.dp, bottomEnd = 14.dp)),
                                        contentScale = ContentScale.Crop
                                    )
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

                        Spacer(Modifier.height(14.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            Arrangement.Center,
                            Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = viewModel::prev,
                                Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    stringResource(R.string.home_prev_book)
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            FloatingActionButton(
                                onClick = viewModel::toggleFavorite,
                                containerColor = orangeBrown,
                                shape = CircleShape,
                                modifier = Modifier.size(52.dp)
                            ) {
                                Icon(
                                    imageVector = if (book.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = stringResource(R.string.home_toggle_favorite),
                                    tint = Color.White
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            IconButton(
                                onClick = viewModel::next,
                                Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    stringResource(R.string.home_next_book)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showOnboarding) {
        HomeOnboardingOverlay(onDismiss = { showOnboarding = false })
    }
}

data class OnboardingPage(
    val title: String,
    val subtitle: String?,
    val body: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeOnboardingOverlay(onDismiss: () -> Unit) {
    val orangeBrown = colorResource(R.color.orange_brown)
    val pages = listOf(
        OnboardingPage("Открой мир книг", "созданный именно для тебя", "Мы подберём книги под твой вкус\n• быстро\n• точно\n• красиво"),
        OnboardingPage("Механика свайпов", null, "Листай книги влево и вправо\n\nПонравилась? Просто лайкни\n\nНе твоё — свайпай дальше"),
        OnboardingPage("Избранное", null, "Все понравившиеся — в твоей личной коллекции\n\nДобавляй книги в избранное и возвращайся к ним в любое время.\n\nКрасиво. Удобно. По уму."),
        OnboardingPage("ИИ-рекомендации", "Твой персональный книжный ИИ-эксперт", "Мы анализируем твои лайки, жанры и стиль чтения, чтобы собирать умную ленту рекомендаций\n\nКаждый свайп делает её точнее"),
        OnboardingPage("Готов?", null, "Пойдём искать твою следующую любимую книгу\n\nТы в одном свайпе от идеального чтения!")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .fillMaxHeight(0.65f)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkGreen)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                }
        ) {
            val pagerState = rememberPagerState(pageCount = { pages.size })
            val coroutineScope = rememberCoroutineScope()

            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) { page ->
                    val pageData = pages[page]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = pageData.title,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        if (pageData.subtitle != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = pageData.subtitle,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = pageData.body,
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(pages.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (isSelected) 24.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) orangeBrown else Color.White.copy(alpha = 0.5f))
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = orangeBrown,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                coroutineScope.launch {
                                    if (pagerState.currentPage < pages.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    } else {
                                        onDismiss()
                                    }
                                }
                            }
                    )
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