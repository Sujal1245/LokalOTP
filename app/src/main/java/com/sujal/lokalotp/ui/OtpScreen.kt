package com.sujal.lokalotp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujal.lokalotp.R
import com.sujal.lokalotp.ui.theme.LokalOTPTheme
import com.sujal.lokalotp.viewmodel.AuthState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    uiState: AuthState.OtpPending,
    onVerifyOtp: (String) -> Unit,
    onResendOtp: () -> Unit
) {
    var otp by rememberSaveable { mutableStateOf("") }

    val currentTimeMillis by produceState(
        initialValue = System.currentTimeMillis(),
        key1 = uiState.expiresAtMillis
    ) {
        while (true) {
            value = System.currentTimeMillis()
            delay(1_000)
        }
    }

    val expiresAt = uiState.expiresAtMillis

    val remainingSeconds = remember(currentTimeMillis, expiresAt) {
        ((expiresAt - currentTimeMillis) / 1000).coerceAtLeast(0)
    }

    val attemptsLeft = uiState.attemptsLeft
    val isExpired = remainingSeconds <= 0

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.otp_screen))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {

            Text(
                text = stringResource(R.string.verify_otp),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "OTP sent to ${uiState.email}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { input ->
                    if (input.length <= 6 && input.all { it.isDigit() }) {
                        otp = input
                    }
                },
                label = { Text(stringResource(R.string.enter_6_digit_otp)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isExpired && attemptsLeft > 0,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (!isExpired)
                    stringResource(R.string.expires_in_s, remainingSeconds)
                else
                    stringResource(R.string.otp_expired),
                color = if (isExpired)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Attempts left: $attemptsLeft",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onVerifyOtp(otp) },
                modifier = Modifier.fillMaxWidth(),
                enabled = otp.length == 6 && attemptsLeft > 0
            ) {
                Text(stringResource(R.string.verify_otp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onResendOtp,
                enabled = isExpired || attemptsLeft < 3
            ) {
                if (isExpired) {
                    Text(stringResource(R.string.resend_otp))
                } else if (attemptsLeft == 3) {
                    Text(stringResource(R.string.resend_otp_please_try_once_before_resending))
                } else {
                    Text(stringResource(R.string.resend_otp_couldn_t_get_the_last_one))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() {
    LokalOTPTheme {
        OtpScreen(
            uiState = AuthState.OtpPending(
                email = "www.sujalmuz1245@gmail.com",
                expiresAtMillis = System.currentTimeMillis() + 60_000L,
                attemptsLeft = 3
            ),
            onVerifyOtp = { },
            onResendOtp = { }
        )
    }
}
