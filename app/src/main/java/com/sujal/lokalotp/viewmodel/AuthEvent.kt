package com.sujal.lokalotp.viewmodel

sealed interface AuthEvent {

    data class NavigateToOtp(val email: String) : AuthEvent

    data class NavigateToSession(val email: String) : AuthEvent

    data object NavigateBackToLogin : AuthEvent

    data class ShowMessage(val message: String) : AuthEvent

}
