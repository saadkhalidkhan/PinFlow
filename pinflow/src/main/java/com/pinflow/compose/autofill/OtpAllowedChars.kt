package com.pinflow.compose.autofill

/**
 * Allowed character set when extracting or validating OTP codes from SMS or clipboard text.
 */
sealed class OtpAllowedChars {
    /** Numeric codes such as `493721`. */
    data object DigitsOnly : OtpAllowedChars()

    /** Alphanumeric codes such as `A7X92B`. */
    data object AlphaNumeric : OtpAllowedChars()
}
