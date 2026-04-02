package com.practicum.vkproject3.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.books.DarkGreen
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val darkGreen = DarkGreen
    val orangeBrown = colorResource(R.color.orange_brown)

    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.onboarding1,
            title = "Твоя идеальная библиотека",
            description = "Мы подберем книги, от которых ты не сможешь оторваться. Быстро и точно"
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding2,
            title = "Свайпай и выбирай",
            description = "Вправо — если хочешь прочитать. Влево — идем дальше. Выбор за тобой"
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding3,
            title = "ИИ знает твои вкусы",
            description = "Умные алгоритмы анализируют твои лайки. Каждый свайп делает ленту еще точнее"
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding4,
            title = "Делись и читай",
            description = "Делись своим мнением в ленте и узнавай мнения других читателей"
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding5,
            title = "Готов к первой главе?",
            description = "Твоя следующая любимая книга уже ждет. Один свайп — и мы начинаем"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.beige_background))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { position ->
            OnboardingPage(pageData = pages[position])
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(targetValue = if (isSelected) 24.dp else 8.dp, label = "indicator_width")

                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (isSelected) darkGreen else darkGreen.copy(alpha = 0.3f))
                    )
                }
            }

            val isLastPage = pagerState.currentPage == pages.size - 1

            val buttonColor by animateColorAsState(
                targetValue = if (isLastPage) orangeBrown else darkGreen,
                label = "btn_color"
            )

            Button(
                onClick = {
                    if (isLastPage) {
                        onFinishOnboarding()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Crossfade(
                    targetState = isLastPage,
                    label = "btn_text"
                ) { last ->
                    Text(
                        text = if (last) "Выбрать жанры" else "Далее",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(pageData: OnboardingPageData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = pageData.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = pageData.title,
            color = colorResource(R.color.text_black),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pageData.description,
            color = colorResource(R.color.text_black).copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}