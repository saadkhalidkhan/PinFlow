package com.pinflow.compose.security

/**
 * Evaluates numeric PIN strength without storing or logging the PIN.
 *
 * The host app is responsible for hashing, encrypting, storing, and verifying PINs.
 */
object PinStrengthValidator {

    private val commonWeakPins = setOf(
        "1234",
        "0000",
        "1111",
        "1212",
        "7777",
        "2580",
    )

    /**
     * Returns the qualitative strength of [pin].
     */
    fun evaluate(pin: String): PinStrength {
        val reason = detectWeakness(pin)
        if (reason != null) return PinStrength.Weak
        return when {
            pin.length >= 6 -> PinStrength.Strong
            pin.length >= 4 -> PinStrength.Medium
            else -> PinStrength.Weak
        }
    }

    /**
     * Validates [pin] against [minStrength]. Returns [PinValidationResult.isValid] when the
     * evaluated strength meets or exceeds [minStrength].
     */
    fun validate(pin: String, minStrength: PinStrength): PinValidationResult {
        val reason = detectWeakness(pin)
        val strength = if (reason != null) {
            PinStrength.Weak
        } else {
            when {
                pin.length >= 6 -> PinStrength.Strong
                pin.length >= 4 -> PinStrength.Medium
                else -> PinStrength.Weak
            }
        }
        val isValid = reason == null && strength.ordinal >= minStrength.ordinal
        return PinValidationResult(
            isValid = isValid,
            strength = strength,
            reason = reason ?: if (!isValid && pin.length < 4) PinWeaknessReason.TooShort else null,
        )
    }

    internal fun detectWeakness(pin: String): PinWeaknessReason? {
        if (pin.isEmpty()) return PinWeaknessReason.TooShort
        if (pin.length < 4) return PinWeaknessReason.TooShort
        if (pin in commonWeakPins) return PinWeaknessReason.CommonPin
        if (hasRepeatedDigits(pin)) return PinWeaknessReason.RepeatedDigits
        if (isAscendingSequence(pin)) return PinWeaknessReason.SequentialAscending
        if (isDescendingSequence(pin)) return PinWeaknessReason.SequentialDescending
        return null
    }

    internal fun hasRepeatedDigits(pin: String): Boolean {
        if (pin.length < 2) return false
        return pin.all { it == pin.first() }
    }

    internal fun isAscendingSequence(pin: String): Boolean {
        if (pin.length < 2) return false
        return pin.zipWithNext().all { (current, next) -> next == current + 1 }
    }

    internal fun isDescendingSequence(pin: String): Boolean {
        if (pin.length < 2) return false
        return pin.zipWithNext().all { (current, next) -> next == current - 1 }
    }
}
