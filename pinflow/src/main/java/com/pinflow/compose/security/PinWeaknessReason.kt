package com.pinflow.compose.security

/**
 * Describes why a PIN is considered weak. Never includes the raw PIN value.
 */
sealed class PinWeaknessReason {
    data object TooShort : PinWeaknessReason()
    data object RepeatedDigits : PinWeaknessReason()
    data object SequentialAscending : PinWeaknessReason()
    data object SequentialDescending : PinWeaknessReason()
    data object CommonPin : PinWeaknessReason()
}
