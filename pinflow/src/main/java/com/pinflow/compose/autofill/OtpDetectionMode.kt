package com.pinflow.compose.autofill

/**
 * Controls how detected OTP codes from SMS or clipboard are presented to the user.
 */
sealed class OtpDetectionMode {
    /** Insert the detected code immediately. */
    data object AutoFill : OtpDetectionMode()

    /** Show a suggestion chip the user can tap to paste. */
    data object Suggestion : OtpDetectionMode()

    /** Ignore detected codes; manual entry only. */
    data object Disabled : OtpDetectionMode()
}
