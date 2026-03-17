package com.practicum.vkproject3.ui.auth

import com.practicum.vkproject3.presentation.auth.ForgotPasswordViewModel
import com.practicum.vkproject3.presentation.auth.ForgotPasswordState
import com.practicum.vkproject3.presentation.auth.ForgotStep

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.profile.BeigeBackground
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

    LaunchedEffect(state.step) {
        if (state.step == ForgotStep.SUCCESS) {
            onSuccessReset()
        }
    }

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
                ForgotStep.CODE -> CodeStep(state, viewModel)
                ForgotStep.NEW_PASSWORD -> NewPasswordStep(state, viewModel)
                ForgotStep.SUCCESS -> { }
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

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = viewModel::submitEmail,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
        shape = RoundedCornerShape(12.dp),
        enabled = !state.isLoading
    ) {
        if (state.isLoading) CircularProgressIndicator(color = Color.White)
        else Text(stringResource(R.string.forgot_password_send_code), fontSize = 16.sp)
    }
}

@Composable
fun CodeStep(state: ForgotPasswordState, viewModel: ForgotPasswordViewModel) {
    Text(stringResource(R.string.forgot_password_enter_code_title), fontSize = 28.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))

    Text(stringResource(R.string.forgot_password_code_sent_to), color = Color.Gray, fontSize = 14.sp)
    Text(
        state.email,
        color = Color(0xFFC26E4B),
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )

    Spacer(modifier = Modifier.height(40.dp))

    OtpInput(
        code = state.code,
        onCodeChange = viewModel::onCodeChange,
        isError = state.codeError != null
    )

    if (state.codeError != null) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(state.codeError, color = ErrorRed, fontSize = 14.sp)
    }

    Spacer(modifier = Modifier.height(40.dp))

    Button(
        onClick = viewModel::submitCode,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
        shape = RoundedCornerShape(12.dp),
        enabled = !state.isLoading
    ) {
        if (state.isLoading) CircularProgressIndicator(color = Color.White)
        else Text(stringResource(R.string.forgot_password_change_button), fontSize = 16.sp)
    }
}

@Composable
fun NewPasswordStep(state: ForgotPasswordState, viewModel: ForgotPasswordViewModel) {
    Text(
        stringResource(R.string.forgot_password_title),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 34.sp
    )
    Spacer(modifier = Modifier.height(60.dp))

    BookTinderTextField(
        value = state.newPassword,
        onValueChange = viewModel::onNewPasswordChange,
        placeholder = stringResource(R.string.placeholder_new_password),
        leadingIcon = Icons.Default.Lock,
        isPassword = true,
        errorMessage = state.passwordError
    )

    Spacer(modifier = Modifier.height(16.dp))

    BookTinderTextField(
        value = state.confirmPassword,
        onValueChange = viewModel::onConfirmPasswordChange,
        placeholder = stringResource(R.string.placeholder_confirm_password),
        leadingIcon = Icons.Default.Lock,
        isPassword = true,
        errorMessage = state.confirmPasswordError
    )

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = viewModel::submitNewPassword,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
        shape = RoundedCornerShape(12.dp),
        enabled = !state.isLoading
    ) {
        if (state.isLoading) CircularProgressIndicator(color = Color.White)
        else Text(stringResource(R.string.forgot_password_change_action), fontSize = 16.sp)
    }
}

@Composable
fun OtpInput(code: String, onCodeChange: (String) -> Unit, isError: Boolean) {
    BasicTextField(
        value = code,
        onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) onCodeChange(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val char = code.getOrNull(index)?.toString() ?: ""
                    val isFocused = code.length == index

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isFocused) Color.White else Color.Transparent)
                            .border(
                                width = if (isError) 2.dp else 1.dp,
                                color = if (isError) ErrorRed else if (isFocused) DarkGreen else Color.Black,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    )
}