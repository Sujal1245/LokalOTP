package com.sujal.lokalotp.data.model

sealed interface OtpValidationResult {
    data object Success : OtpValidationResult
    data object Expired : OtpValidationResult
    data object Invalid : OtpValidationResult
    data object AttemptsExceeded : OtpValidationResult
    data object NoOtpFound : OtpValidationResult
}
