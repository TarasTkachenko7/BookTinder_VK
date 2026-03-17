package com.practicum.vkproject3.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.practicum.vkproject3.presentation.auth.VerificationViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.profile.BeigeBackground
import com.practicum.vkproject3.ui.theme.DarkGreen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationScreen(
    email: String,
    viewModel: VerificationViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isVerified) {
        if (state.isVerified) onSuccess()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.verification_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = BeigeBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.verification_text),
                textAlign = TextAlign.Center, fontSize = 16.sp
            )
            Text(
                text = email,
                color = Color(0xFFC26E4B),
                fontWeight = FontWeight.Bold, fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            BookTinderTextField(
                value = state.code,
                onValueChange = { viewModel.onCodeChange(it) },
                placeholder = stringResource(R.string.placeholder_code),
                leadingIcon = Icons.Default.CheckCircle,
                errorMessage = state.error
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.verify() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.verification_button), fontSize = 18.sp)
                }
            }
        }
    }
}