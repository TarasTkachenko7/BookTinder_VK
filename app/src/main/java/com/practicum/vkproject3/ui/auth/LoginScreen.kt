package com.practicum.vkproject3.ui.auth

import com.practicum.vkproject3.presentation.auth.LoginViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.profile.BeigeBackground
import com.practicum.vkproject3.ui.theme.DarkGreen
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(
    onNavigateToRegistration: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToForgot: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.login_title),
            fontSize = 24.sp,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = DarkGreen
        )

        Spacer(modifier = Modifier.height(48.dp))

        BookTinderTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = stringResource(R.string.placeholder_email),
            leadingIcon = Icons.Default.Email,
            errorMessage = state.emailError
        )

        Spacer(modifier = Modifier.height(32.dp))

        BookTinderTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = stringResource(R.string.placeholder_password),
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            errorMessage = state.passwordError ?: state.errorMessage
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = stringResource(R.string.login_forgot_password),
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { onNavigateToForgot() }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = viewModel::login,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(stringResource(R.string.login_button), fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row (){
            Text(text = stringResource(R.string.login_no_account), color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.login_create_account),
                color = DarkGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegistration() }
            )
        }
    }
}