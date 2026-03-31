package com.practicum.vkproject3.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
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
    val configuration = LocalConfiguration.current

    val genreIds = stringArrayResource(id = R.array.genre_ids)

    val genreNames = remember(configuration) {
        val resIds = context.resources.obtainTypedArray(R.array.genre_name_res_ids)
        val names = Array(genreIds.size) { i -> context.getString(resIds.getResourceId(i, 0)) }
        resIds.recycle()
        names
    }

    var showGenreSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            CenterAlignedTopAppBar(
                title = { Text("Редактировать профиль", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BeigeBackground)
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
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
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
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MainBrown)
                            .border(2.dp, BeigeBackground, CircleShape)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { newValue ->
                        name = newValue.filter { char ->
                            char in 'а'..'я' || char in 'А'..'Я' || char == 'ё' || char == 'Ё' || char in 'a'..'z' || char in 'A'..'Z' || char == ' '
                        }
                    },
                    label = { Text("Ваше имя") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MainBrown,
                        focusedLabelColor = MainBrown,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Любимые жанры",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                    TextButton(onClick = { showGenreSheet = true }) {
                        Text("Изменить", color = MainBrown, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedGenres.isEmpty()) {
                    Text(
                        text = "Вы еще не выбрали жанры",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedGenres.forEach { id ->
                            val index = genreIds.indexOf(id)
                            if (index != -1) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MainBrown.copy(alpha = 0.1f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MainBrown.copy(alpha = 0.3f)),
                                    modifier = Modifier.clickable { selectedGenres.remove(id) }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = genreNames[index],
                                            color = MainBrown,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            tint = MainBrown,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        val avatarToSave = selectedImageUri?.toString() ?: state.user?.avatarUrl
                        viewModel.updateProfile(name, selectedGenres.toList(), avatarToSave)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainBrown),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Сохранить изменения", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                val dullBrown = Color(0xFF9E8E85)
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = dullBrown),
                    border = androidx.compose.foundation.BorderStroke(1.dp, dullBrown),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Выйти из аккаунта", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { viewModel.deleteAccount() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Удалить аккаунт", color = Color.Red.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
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

        if (showGenreSheet) {
            ModalBottomSheet(
                onDismissRequest = { showGenreSheet = false },
                sheetState = sheetState,
                containerColor = BeigeBackground
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Выберите жанры",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(0.6f)
                    ) {
                        itemsIndexed(genreIds) { index, id ->
                            val isChecked = selectedGenres.contains(id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedGenres.remove(id)
                                        else selectedGenres.add(id)
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(checkedColor = MainBrown)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = genreNames[index], fontSize = 16.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showGenreSheet = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MainBrown),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Готово")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}