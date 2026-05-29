package com.practicum.vkproject3.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.practicum.vkproject3.presentation.home.HomeViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontStyle
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
    val bookQuotes = stringArrayResource(R.array.home_quotes).toList()

    val pagerState = rememberPagerState(
        initialPage = state.index,
        pageCount = { if (state.books.isEmpty()) 0 else state.books.size + 1 }
    )

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != state.index) {
            viewModel.setIndex(pagerState.currentPage)
        }
    }

    LaunchedEffect(state.index) {
        if (state.index != pagerState.currentPage && state.index < state.books.size) {
            pagerState.animateScrollToPage(state.index)
        }
    }

    val baseSheetPeekHeight = 140.dp
    val currentPeekHeight = if (state.current == null) 0.dp else baseSheetPeekHeight

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.PartiallyExpanded, skipHiddenState = true)
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        containerColor = beigeBackground,
        sheetContainerColor = DarkGreen,
        sheetPeekHeight = currentPeekHeight,
        sheetDragHandle = {
            if (currentPeekHeight > 0.dp) {
                Box(
                    Modifier
                        .padding(top = 8.dp)
                        .width(60.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(orangeBrown)
                )
            }
        },
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(4.dp))
                Text(text = stringResource(R.string.home_about_book), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    val description = state.current?.description ?: stringResource(R.string.description_absent)
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
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Column(Modifier.fillMaxSize()) {

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
                    }
                }
                Spacer(Modifier.height(14.dp))

                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when {
                        state.isLoading && state.books.isEmpty() -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = DarkGreen)
                                    Spacer(Modifier.height(12.dp))
                                    Text(stringResource(R.string.home_loading))
                                }
                            }
                        }
                        state.error != null && state.books.isEmpty() -> {
                            Column(
                                Modifier.fillMaxSize(),
                                Arrangement.Center,
                                Alignment.CenterHorizontally
                            ) {
                                Text(stringResource(R.string.error_book_load))
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = viewModel::loadAiBooks,
                                    colors = ButtonDefaults.buttonColors(containerColor = orangeBrown)
                                ) { Text(stringResource(R.string.home_retry)) }
                            }
                        }
                        state.isEmpty -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(R.string.home_no_books)) }
                        }
                        else -> {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { page ->
                                if (page < state.books.size) {
                                    val book = state.books[page]
                                    var showLikeAnimation by remember { mutableStateOf(false) }

                                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Card(
                                            Modifier
                                                .fillMaxWidth(0.75f)
                                                .height(500.dp)
                                                .pointerInput(Unit) {
                                                    detectTapGestures(
                                                        onDoubleTap = {
                                                            if (state.index == page) {
                                                                viewModel.toggleFavorite()
                                                                showLikeAnimation = true
                                                            }
                                                        }
                                                    )
                                                },
                                            RoundedCornerShape(18.dp),
                                            CardDefaults.cardColors(containerColor = DarkGreen)
                                        ) {
                                            Box(Modifier.fillMaxSize()) {
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
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Star,
                                                                    contentDescription = null,
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text(
                                                                    text = stringResource(R.string.home_rating_format, book.rating),
                                                                    color = Color.White.copy(alpha = 0.9f),
                                                                    fontSize = 13.sp,
                                                                    fontWeight = FontWeight.Medium
                                                                )
                                                            }

                                                            Text(
                                                                text = book.genreId,
                                                                color = Color.White.copy(alpha = 0.9f),
                                                                fontSize = 13.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    }
                                                }

                                                androidx.compose.animation.AnimatedVisibility(
                                                    visible = showLikeAnimation,
                                                    enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(100)),
                                                    exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(300)),
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.3f)))
                                                }

                                                androidx.compose.animation.AnimatedVisibility(
                                                    visible = showLikeAnimation,
                                                    enter = scaleIn(initialScale = 0.5f) + fadeIn(),
                                                    exit = scaleOut(targetScale = 1.5f) + fadeOut(),
                                                    modifier = Modifier.align(Alignment.Center)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Favorite,
                                                        contentDescription = stringResource(R.string.home_liked_description),
                                                        tint = orangeBrown,
                                                        modifier = Modifier.size(120.dp)
                                                    )
                                                }
                                            }

                                            LaunchedEffect(showLikeAnimation) {
                                                if (showLikeAnimation) {
                                                    delay(600)
                                                    showLikeAnimation = false
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (state.isLoading) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                CircularProgressIndicator(color = DarkGreen)
                                                Spacer(Modifier.height(12.dp))
                                                Text(stringResource(R.string.home_loading))
                                            }
                                        }
                                    } else if (state.error != null) {
                                        Column(
                                            Modifier.fillMaxSize(),
                                            Arrangement.Center,
                                            Alignment.CenterHorizontally
                                        ) {
                                            Text(stringResource(R.string.home_error_recommendations))
                                            Spacer(Modifier.height(12.dp))
                                            Button(
                                                onClick = viewModel::loadAiBooks,
                                                colors = ButtonDefaults.buttonColors(containerColor = orangeBrown)
                                            ) { Text(stringResource(R.string.home_retry)) }
                                        }
                                    } else if (state.isExhausted) {
                                        val randomQuote = remember { bookQuotes.random() }
                                        Column(
                                            modifier = Modifier.fillMaxSize().padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = stringResource(R.string.home_exhausted_title),
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textBlack,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = stringResource(R.string.home_exhausted_subtitle),
                                                fontSize = 16.sp,
                                                color = textBlack.copy(alpha = 0.7f),
                                                textAlign = TextAlign.Center,
                                                lineHeight = 22.sp
                                            )
                                            Spacer(modifier = Modifier.height(32.dp))
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = beigeBackground.copy(alpha = 0.5f)),
                                                border = BorderStroke(1.dp, orangeBrown.copy(alpha = 0.3f)),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = randomQuote,
                                                    modifier = Modifier.padding(16.dp),
                                                    fontSize = 14.sp,
                                                    fontStyle = FontStyle.Italic,
                                                    color = textBlack.copy(alpha = 0.8f),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(48.dp))
                                            Button(
                                                onClick = viewModel::prev,
                                                colors = ButtonDefaults.buttonColors(containerColor = orangeBrown)
                                            ) {
                                                Text(stringResource(R.string.home_back_to_previous), color = Color.White)
                                            }
                                        }
                                    } else {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = DarkGreen)
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
        OnboardingPage(
            stringResource(R.string.home_onboarding_page_1_title),
            stringResource(R.string.home_onboarding_page_1_subtitle),
            stringResource(R.string.home_onboarding_page_1_body)
        ),
        OnboardingPage(
            stringResource(R.string.home_onboarding_page_2_title),
            null,
            stringResource(R.string.home_onboarding_page_2_body)
        ),
        OnboardingPage(
            stringResource(R.string.home_onboarding_page_3_title),
            null,
            stringResource(R.string.home_onboarding_page_3_body)
        ),
        OnboardingPage(
            stringResource(R.string.home_onboarding_page_4_title),
            stringResource(R.string.home_onboarding_page_4_subtitle),
            stringResource(R.string.home_onboarding_page_4_body)
        ),
        OnboardingPage(
            stringResource(R.string.home_onboarding_page_5_title),
            null,
            stringResource(R.string.home_onboarding_page_5_body)
        )
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
                        contentDescription = stringResource(R.string.home_close),
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
                        contentDescription = stringResource(R.string.home_next_book),
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



