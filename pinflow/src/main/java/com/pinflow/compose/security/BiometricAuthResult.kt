package com.pinflow.compose.security

/**
 * Outcome of a [BiometricAuthenticator] prompt.
 */
sealed class BiometricAuthResult {
    data object Success : BiometricAuthResult()
    data class Error(val code: Int, val message: String) : BiometricAuthResult()
    data object Failed : BiometricAuthResult()
    data object Cancelled : BiometricAuthResult()
}
