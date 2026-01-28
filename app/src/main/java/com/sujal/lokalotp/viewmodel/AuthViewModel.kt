package com.sujal.lokalotp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sujal.lokalotp.analytics.AnalyticsLogger
import com.sujal.lokalotp.data.OtpManager
import com.sujal.lokalotp.data.model.OtpValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val otpManager: OtpManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.LoggedOut)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private suspend fun generateOtpInternal(email: String) {
        delay(2.seconds) // Let's assume "real" OTP generation takes 2 seconds

        withContext(Dispatchers.IO) {
            /* Switching Dispatcher as if it is a network call,
            although it is not, and rather a simple operation. */
            val otp = otpManager.generateOtp(email)
            AnalyticsLogger.otpGenerated(email, otp)
        }

        val expiresAt = System.currentTimeMillis() + OTP_EXPIRY_MILLIS

        _authState.value = AuthState.OtpPending(
            email = email,
            expiresAtMillis = expiresAt,
            attemptsLeft = MAX_ATTEMPTS
        )
    }

    fun sendOtp(email: String) {
        viewModelScope.launch {
            generateOtpInternal(email)

            _events.send(AuthEvent.NavigateToOtp(email))
        }
    }

    fun resendOtp(email: String) {
        viewModelScope.launch {
            generateOtpInternal(email)

            _events.send(AuthEvent.ShowMessage("A new OTP sent."))
        }
    }

    fun verifyOtp(email: String, enteredOtp: String) {
        viewModelScope.launch {
            when (otpManager.validateOtp(email, enteredOtp)) {

                OtpValidationResult.Success -> {
                    AnalyticsLogger.otpValidationSuccess(email)
                    val sessionStart = System.currentTimeMillis()

                    otpManager.clearStore()

                    _authState.value = AuthState.LoggedIn(
                        email = email,
                        sessionStartMillis = sessionStart
                    )

                    _events.send(AuthEvent.NavigateToSession(email))
                }

                OtpValidationResult.Invalid -> {
                    AnalyticsLogger.otpValidationFailure(email, "invalid_otp")
                    val current = _authState.value as? AuthState.OtpPending ?: return@launch
                    val updatedAttempts = current.attemptsLeft - 1

                    _authState.value = current.copy(attemptsLeft = updatedAttempts)

                    _events.send(
                        AuthEvent.ShowMessage("Invalid OTP. Attempts left: $updatedAttempts")
                    )
                }

                OtpValidationResult.Expired -> {
                    AnalyticsLogger.otpValidationFailure(email, "expired")

                    _events.send(
                        AuthEvent.ShowMessage("OTP expired. Please request a new one.")
                    )
                }

                OtpValidationResult.AttemptsExceeded -> {
                    AnalyticsLogger.otpValidationFailure(email, "attempts_exceeded")

                    val current = _authState.value as? AuthState.OtpPending ?: return@launch
                    _authState.value = current.copy(attemptsLeft = 0)

                    _events.send(
                        AuthEvent.ShowMessage("Maximum attempts exceeded.")
                    )
                }

                OtpValidationResult.NoOtpFound -> {
                    AnalyticsLogger.otpValidationFailure(email, "no_otp_found")

                    _events.send(
                        AuthEvent.ShowMessage("No OTP found. Please request a new one.")
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            AnalyticsLogger.logout()
            _authState.value = AuthState.LoggedOut
            _events.send(AuthEvent.NavigateBackToLogin)
        }
    }

    companion object {
        private const val OTP_EXPIRY_MILLIS = 60_000L
        private const val MAX_ATTEMPTS = 3
    }
}
