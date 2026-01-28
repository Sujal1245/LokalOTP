package com.sujal.lokalotp.navigation

sealed class AuthRoute(val route: String) {

    data object Login : AuthRoute("login")

    data object Otp : AuthRoute("otp")

    data object Session : AuthRoute("session")
}
