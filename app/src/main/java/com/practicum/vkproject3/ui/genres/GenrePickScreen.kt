package com.practicum.vkproject3.ui.genres

import com.practicum.vkproject3.presentation.genres.GenrePickViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.theme.BeigeBackground
import com.practicum.vkproject3.ui.theme.DarkGreen
import androidx.compose.ui.res.colorResource
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenrePickScreen(
    onDone: () -> Unit,
    viewModel: GenrePickViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 24.dp)
            .verticalScroll(scroll),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))

        Text(
            text = stringResource(R.string.genre_pick_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.genre_pick_subtitle),
            fontSize = 16.sp,
            color = Color.Black.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = DarkGreen)
            Spacer(Modifier.height(24.dp))
        } else if (state.error != null) {
            Text(text = state.error!!, color = Color.Red)
            Spacer(Modifier.height(24.dp))
        } else {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Используем объект genre напрямую из стейта
                state.genres.forEach { genre ->
                    val isSelected = state.selected.contains(genre.id)

                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.toggleGenre(genre.id) },
                        label = {
                            Text(
                                text = genre.name, // Берем имя жанра из базы
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkGreen,
                            selectedLabelColor = Color.White,
                            containerColor = colorResource(R.color.chip_unselected),
                            labelColor = Color.Black.copy(alpha = 0.7f)
                        ),
                        shape = MaterialTheme.shapes.large,
                        border = null
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        Button(
            onClick = {
                viewModel.saveSelectedGenres()
                onDone()
            },
            enabled = state.canSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.button_brown)),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = stringResource(R.string.genre_pick_button), fontSize = 18.sp)
        }

        Spacer(Modifier.height(24.dp))
    }
}