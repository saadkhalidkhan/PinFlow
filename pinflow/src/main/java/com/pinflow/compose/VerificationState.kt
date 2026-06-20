package com.pinflow.compose

/**
 * External verification lifecycle for [OtpInput] — drives progress, error shake, and success wave.
 */
sealed class VerificationState {
    data object Idle : VerificationState()
    data object Typing : VerificationState()
    data object Verifying : VerificationState()
    data object Success : VerificationState()
    data class Error(val message: String? = null) : VerificationState()
}
