package com.sujal.lokalotp.data.model

data class OtpEntry(
    val otp: String,
    val generatedAtMillis: Long,
    val attemptsLeft: Int
)
