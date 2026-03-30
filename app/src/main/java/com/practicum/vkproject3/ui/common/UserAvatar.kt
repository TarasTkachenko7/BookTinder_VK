package com.practicum.vkproject3.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.vkproject3.ui.theme.Cream
import com.practicum.vkproject3.ui.theme.DarkGreen
import com.practicum.vkproject3.ui.theme.DustyBlue
import com.practicum.vkproject3.ui.theme.MainBrown
import com.practicum.vkproject3.ui.theme.SageGreen
import com.practicum.vkproject3.ui.theme.WarmSand

@Composable
fun UserAvatar(
    nickname: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.Transparent,
    borderBrush: Brush? = null,
    placeholderContent: (@Composable BoxScope.() -> Unit)? = null
) {
    val fallbackColor = remember(nickname) { nickname.toAvatarPlaceholderColor() }

    val borderModifier = when {
        borderBrush != null -> Modifier.border(borderWidth, borderBrush, CircleShape)
        borderWidth > 0.dp -> Modifier.border(borderWidth, borderColor, CircleShape)
        else -> Modifier
    }

    Box(
        modifier = modifier
            .then(borderModifier)
            .padding(if (borderWidth > 0.dp) 4.dp else 0.dp)
            .clip(CircleShape)
            .background(Cream),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Аватар пользователя $nickname",
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
                    .background(fallbackColor),
                contentAlignment = Alignment.Center
            ) {
                if (placeholderContent != null) {
                    placeholderContent()
                } else {
                    Text(
                        text = nickname.avatarInitial(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun String.toAvatarPlaceholderColor(): Color {
    val colors = listOf(
        MainBrown,
        DarkGreen,
        WarmSand,
        SageGreen,
        DustyBlue
    )
    return colors[(hashCode().ushr(1)) % colors.size]
}

fun String.avatarInitial(): String {
    return trim().take(1).uppercase()
}