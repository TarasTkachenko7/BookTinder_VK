package com.practicum.vkproject3.ui.auth

import com.practicum.vkproject3.presentation.auth.RegistrationViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.vkproject3.R
import com.practicum.vkproject3.ui.profile.BeigeBackground
import com.practicum.vkproject3.ui.theme.DarkGreen
import com.practicum.vkproject3.ui.theme.ErrorRed
import org.koin.androidx.compose.koinViewModel


@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = koinViewModel(),
    onNavigateToVerification: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToVerification(state.email)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(R.string.registration_title),
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
            errorMessage = state.passwordError
        )

        Spacer(modifier = Modifier.height(32.dp))

        BookTinderTextField(
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            placeholder = stringResource(R.string.placeholder_confirm_password),
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            errorMessage = state.confirmPasswordError
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { viewModel.register() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
            shape = RoundedCornerShape(12.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(R.string.registration_button), fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(text = stringResource(R.string.registration_has_account), color = Color.Black)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.registration_login),
                color = DarkGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}

@Composable
fun BookTinderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(!isPassword) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFEBEBEB),
                unfocusedContainerColor = Color(0xFFEBEBEB),
                disabledContainerColor = Color(0xFFEBEBEB),
                focusedBorderColor = if (errorMessage != null) ErrorRed else Color.Black,
                unfocusedBorderColor = if (errorMessage != null) ErrorRed else Color.Transparent,
                errorBorderColor = ErrorRed,
                cursorColor = DarkGreen
            ),
            leadingIcon = {
                Icon(imageVector = leadingIcon, contentDescription = null, tint = Color.Gray)
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = stringResource(R.string.content_description_toggle_password),
                            tint = Color.Gray
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = ErrorRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
