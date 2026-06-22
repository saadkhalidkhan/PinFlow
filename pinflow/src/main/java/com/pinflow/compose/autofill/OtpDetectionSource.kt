package com.pinflow.compose.autofill

/** Origin of a detected OTP code. */
sealed class OtpDetectionSource {
    data object Sms : OtpDetectionSource()
    data object Clipboard : OtpDetectionSource()
}
