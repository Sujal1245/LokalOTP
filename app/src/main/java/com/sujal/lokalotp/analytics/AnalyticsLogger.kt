package com.sujal.lokalotp.analytics

import timber.log.Timber

object AnalyticsLogger {

    fun otpGenerated(email: String, otp: String) {
        Timber.d("OTP generated for email=%s:%s", email, otp)
    }

    fun otpValidationSuccess(email: String) {
        Timber.i("OTP validation SUCCESS for email=%s", email)
    }

    fun otpValidationFailure(email: String, reason: String) {
        Timber.w(
            "OTP validation FAILURE for email=%s, reason=%s",
            email,
            reason
        )
    }

    fun logout() {
        Timber.i("User logged out.")
    }
}