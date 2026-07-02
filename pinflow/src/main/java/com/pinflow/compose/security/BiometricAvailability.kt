package com.pinflow.compose.security

/**
 * Biometric hardware and enrollment status on the current device.
 */
sealed class BiometricAvailability {
    data object Available : BiometricAvailability()
    data object Unavailable : BiometricAvailability()
    data object NoHardware : BiometricAvailability()
    data object NoneEnrolled : BiometricAvailability()
    data object SecurityUpdateRequired : BiometricAvailability()
    data object Unsupported : BiometricAvailability()
    data object Unknown : BiometricAvailability()
}
