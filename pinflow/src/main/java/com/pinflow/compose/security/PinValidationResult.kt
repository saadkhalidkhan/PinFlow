package com.pinflow.compose.security

/**
 * Outcome of validating a PIN against a minimum strength requirement.
 */
data class PinValidationResult(
    val isValid: Boolean,
    val strength: PinStrength,
    val reason: PinWeaknessReason? = null,
)
