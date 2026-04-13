package com.practicum.vkproject3.ui.auth

import com.practicum.vkproject3.presentation.auth.ForgotPasswordViewModel
import com.practicum.vkproject3.presentation.auth.ForgotPasswordState
import com.practicum.vkproject3.presentation.auth.ForgotStep

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import com.practicum.vkproject3.ui.theme.ErrorRed
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = koinViewModel(),
    onBack: () -> Unit,
    onSuccessReset: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            when (state.step) {
                ForgotStep.EMAIL -> EmailStep(state, viewModel)
                ForgotStep.SUCCESS -> {
                    Text(
                        text = "Письмо для сброса пароля отправлено на вашу почту!",
                        color = DarkGreen,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Вернуться к логину", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun EmailStep(state: ForgotPasswordState, viewModel: ForgotPasswordViewModel) {
    Text(
        stringResource(R.string.forgot_password_title),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 34.sp
    )
    Spacer(modifier = Modifier.height(60.dp))

    BookTinderTextField(
        value = state.email,
        onValueChange = viewModel::onEmailChange,
        placeholder = stringResource(R.string.placeholder_email),
        leadingIcon = Icons.Default.Email,
        errorMessage = state.emailError
    )

    if (state.errorMessage != null) {
        Text(
            text = state.errorMessage,
            color = ErrorRed,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = viewModel::submitEmail,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
        shape = RoundedCornerShape(12.dp),
        enabled = !state.isLoading
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text(stringResource(R.string.forgot_password_send_code), fontSize = 16.sp)
        }
    }
}
