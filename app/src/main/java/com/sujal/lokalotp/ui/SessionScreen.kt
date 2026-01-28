package com.sujal.lokalotp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujal.lokalotp.R
import com.sujal.lokalotp.ui.theme.LokalOTPTheme
import com.sujal.lokalotp.viewmodel.AuthState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun SessionScreen(
    uiState: AuthState.LoggedIn,
    onLogout: () -> Unit
) {
    val sessionStartMillis = uiState.sessionStartMillis

    // Tick every second to update elapsed time
    val currentTimeMillis by produceState(
        initialValue = System.currentTimeMillis(),
        key1 = sessionStartMillis
    ) {
        while (true) {
            System.currentTimeMillis().also { value = it }
            delay(1_000)
        }
    }

    val elapsedSeconds = remember(currentTimeMillis, sessionStartMillis) {
        ((currentTimeMillis - sessionStartMillis) / 1000).coerceAtLeast(0)
    }

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.session_screen))
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.session_active),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Logged in as ${uiState.email}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = String.format(stringResource(R.string.session_duration_02d_02d), minutes, seconds),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.logout))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionScreenPreview() {
    LokalOTPTheme {
        SessionScreen(
            uiState = AuthState.LoggedIn(
                email = "www.sujalmuz1245@gmail.com",
                sessionStartMillis = System.currentTimeMillis() - 10_000L
            ),
            onLogout = { }
        )
    }
}
