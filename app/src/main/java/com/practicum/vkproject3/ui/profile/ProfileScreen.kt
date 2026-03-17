package com.practicum.vkproject3.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.theme.MainBrown

val BeigeBackground = Color(0xFFFDF8F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHistory: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    val figmaGradient = Brush.linearGradient(
        0.0f to Color(0xFFED15B0),
        0.5f to Color(0xFF1D12E3).copy(alpha = 0.8f),
        1.0f to Color(0xFFEE0F13)
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(38.dp))

                Row {
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(androidx.compose.ui.res.colorResource(R.color.icon_gray).copy(alpha = 0.9f))
                            .clickable { },
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
        },
        containerColor = BeigeBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .border(width = 4.dp, brush = figmaGradient, shape = CircleShape)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.spotty),
                    contentDescription = stringResource(R.string.content_description_avatar),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.profile_name),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            MenuButton(text = stringResource(R.string.profile_edit), backgroundColor = MainBrown)
            Spacer(modifier = Modifier.height(12.dp))

            MenuButton(text = stringResource(R.string.profile_subscription), backgroundBrush = figmaGradient)
            Spacer(modifier = Modifier.height(12.dp))

            MenuButton(text = stringResource(R.string.profile_history), backgroundColor = MainBrown, onClick = onNavigateToHistory)
            Spacer(modifier = Modifier.height(12.dp))

            MenuButton(text = stringResource(R.string.profile_settings), backgroundColor = MainBrown)
            Spacer(modifier = Modifier.height(12.dp))

            MenuButton(text = stringResource(R.string.profile_support), backgroundColor = MainBrown)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    backgroundColor: Color? = null,
    backgroundBrush: Brush? = null,
    onClick: () -> Unit = {}
) {
    val backgroundModifier = if (backgroundBrush != null) {
        Modifier.background(backgroundBrush, shape = RoundedCornerShape(12.dp))
    } else {
        Modifier.background(backgroundColor ?: MainBrown, shape = RoundedCornerShape(12.dp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .then(backgroundModifier)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White
        )
    }
}