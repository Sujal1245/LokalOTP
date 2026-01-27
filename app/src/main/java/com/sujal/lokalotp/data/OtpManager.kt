package com.sujal.lokalotp.data

import com.sujal.lokalotp.data.model.OtpEntry
import com.sujal.lokalotp.data.model.OtpValidationResult
import kotlin.random.Random

class OtpManager {
    companion object {
        private const val OTP_LENGTH = 6
        private const val OTP_EXPIRY_MILLIS = 60_000L
        private const val MAX_ATTEMPTS = 3
    }

    private val otpStore = mutableMapOf<String, OtpEntry>()

    fun generateOtp(email: String): String {
        val otp = generateSixDigitOtp()
        val entry = OtpEntry(
            otp = otp,
            generatedAtMillis = System.currentTimeMillis(),
            attemptsLeft = MAX_ATTEMPTS
        )
        otpStore[email] = entry
        return otp
    }

    fun validateOtp(
        email: String,
        enteredOtp: String
    ): OtpValidationResult {
        val entry = otpStore[email] ?: return OtpValidationResult.NoOtpFound

        val now = System.currentTimeMillis()
        val timeCrossed = now - entry.generatedAtMillis

        // Expiry check
        if (timeCrossed > OTP_EXPIRY_MILLIS) {
            otpStore.remove(email)
            return OtpValidationResult.Expired
        }

        // Attempts exhausted check
        if (entry.attemptsLeft <= 0) {
            otpStore.remove(email)
            return OtpValidationResult.AttemptsExceeded
        }

        // OTP match
        if (entry.otp == enteredOtp) {
            otpStore.remove(email)
            return OtpValidationResult.Success
        }

        // Incorrect OTP: decrement attempts
        val updatedEntry = entry.copy(
            attemptsLeft = entry.attemptsLeft - 1
        )
        otpStore[email] = updatedEntry

        return if (updatedEntry.attemptsLeft <= 0) {
            otpStore.remove(email)
            OtpValidationResult.AttemptsExceeded
        } else {
            OtpValidationResult.Invalid
        }
    }

    private fun generateSixDigitOtp(): String {
        val number = Random.nextInt(0, 1_000_000)
        return number.toString().padStart(OTP_LENGTH, '0')
    }
}
