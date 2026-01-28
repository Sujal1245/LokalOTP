package com.sujal.lokalotp.viewmodel

sealed interface AuthState {

    data object LoggedOut : AuthState

    data class OtpPending(
        val email: String,
        val expiresAtMillis: Long,
        val attemptsLeft: Int
    ) : AuthState

    data class LoggedIn(
        val email: String,
        val sessionStartMillis: Long
    ) : AuthState
}
