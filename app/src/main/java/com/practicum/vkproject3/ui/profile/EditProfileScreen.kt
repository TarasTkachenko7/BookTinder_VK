package com.practicum.vkproject3.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.vkproject3.R
import com.practicum.vkproject3.presentation.profile.ProfileViewModel
import com.practicum.vkproject3.ui.theme.MainBrown
import com.practicum.vkproject3.ui.theme.BeigeBackground
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var name by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val selectedGenres = remember { mutableStateListOf<String>() }
    val scrollState = rememberScrollState()
    var isInitialized by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val genreIds = remember { context.resources.getStringArray(R.array.genre_ids) }
    val genreNames = remember {
        val resIds = context.resources.obtainTypedArray(R.array.genre_name_res_ids)
        val names = Array(genreIds.size) { i -> context.getString(resIds.getResourceId(i, 0)) }
        resIds.recycle()
        names
    }

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.user) {
        if (state.user != null && !isInitialized) {
            name = state.user?.name ?: ""
            selectedGenres.clear()
            selectedGenres.addAll(state.user?.favoriteGenres ?: emptyList())
            isInitialized = true
        }
    }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogout()
        }
    }

    LaunchedEffect(state.isUpdateSuccess) {
        if (state.isUpdateSuccess) {
            viewModel.resetUpdateSuccess()
            onBack()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            android.widget.Toast.makeText(context, errorMessage, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Редактировать профиль", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BeigeBackground)
            )
        },
        containerColor = BeigeBackground
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (state.user?.avatarUrl != null) {
                        AsyncImage(
                            model = state.user?.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.spotty),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { newValue ->
                        name = newValue.filter { char ->
                            char in 'а'..'я' ||
                                    char in 'А'..'Я' ||
                                    char == 'ё' ||
                                    char == 'Ё' ||
                                    char in 'a'..'z' ||
                                    char in 'A'..'Z' ||
                                    char == ' '
                        }
                    },
                    label = { Text("Ваше имя") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainBrown,
                        focusedLabelColor = MainBrown
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Любимые жанры",
                    modifier = Modifier.align(Alignment.Start),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedGenres.isEmpty()) "Выберите жанры" else "Выбрано жанров: ${selectedGenres.size}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MainBrown,
                            focusedLabelColor = MainBrown
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        genreIds.forEachIndexed { index, id ->
                            val isChecked = selectedGenres.contains(id)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = null,
                                            colors = CheckboxDefaults.colors(checkedColor = MainBrown)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = genreNames[index])
                                    }
                                },
                                onClick = {
                                    if (isChecked) selectedGenres.remove(id)
                                    else selectedGenres.add(id)
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val avatarToSave = selectedImageUri?.toString() ?: state.user?.avatarUrl
                        viewModel.updateProfile(name, selectedGenres.toList(), avatarToSave)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MainBrown),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Сохранить изменения", modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                val dullBrown = Color(0xFF9E8E85)
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = dullBrown),
                    border = androidx.compose.foundation.BorderStroke(1.dp, dullBrown),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Выйти из аккаунта", modifier = Modifier.padding(8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { viewModel.deleteAccount() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить аккаунт", color = Color.Red.copy(alpha = 0.6f), fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }

            if (state.isLoading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MainBrown)
                }
            }
        }
    }
}