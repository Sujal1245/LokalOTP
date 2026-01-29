package com.sujal.lokalotp.navigation

import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sujal.lokalotp.ui.LoadingScreen
import com.sujal.lokalotp.ui.LoginScreen
import com.sujal.lokalotp.ui.OtpScreen
import com.sujal.lokalotp.ui.SessionScreen
import com.sujal.lokalotp.viewmodel.AuthEvent
import com.sujal.lokalotp.viewmodel.AuthState
import com.sujal.lokalotp.viewmodel.AuthViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        authViewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateToOtp -> {
                    navController.navigate(AuthRoute.Otp.route){
                        launchSingleTop = true
                    }
                }

                is AuthEvent.NavigateToSession -> {
                    navController.navigate(AuthRoute.Session.route) {
                        launchSingleTop = true
                        popUpTo(AuthRoute.Login.route) {
                            inclusive = true
                        }
                    }
                }

                AuthEvent.NavigateBackToLogin -> {
                    navController.navigate(AuthRoute.Login.route) {
                        popUpTo(AuthRoute.Session.route) {
                            inclusive = true
                        }
                    }
                }

                is AuthEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = AuthRoute.Login.route,
            modifier = Modifier.padding(
                top = 0.dp,
                bottom = paddingValues.calculateBottomPadding(),
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = paddingValues.calculateStartPadding(LayoutDirection.Ltr)
            )
        ) {

            composable(AuthRoute.Login.route) {
                LoginScreen(
                    onSendOtp = { email ->
                        authViewModel.sendOtp(email)
                    }
                )
            }

            composable(
                route = AuthRoute.Otp.route
            ) {
                when (authState) {
                    is AuthState.OtpPending -> {

                        val otpPendingState = authState as AuthState.OtpPending

                        OtpScreen(
                            uiState = otpPendingState,
                            onVerifyOtp = { otp ->
                                authViewModel.verifyOtp(otpPendingState.email, otp)
                            },
                            onResendOtp = {
                                authViewModel.resendOtp(otpPendingState.email)
                            }
                        )
                    }

                    else -> {
                        LoadingScreen()
                    }
                }
            }

            composable(
                route = AuthRoute.Session.route
            ) {

                when (authState) {
                    is AuthState.LoggedIn -> {

                        val loggedInState = authState as AuthState.LoggedIn

                        SessionScreen(
                            uiState = loggedInState,
                            onLogout = {
                                authViewModel.logout()
                            }
                        )
                    }

                    else -> {
                        LoadingScreen()
                    }
                }
            }
        }
    }
}
