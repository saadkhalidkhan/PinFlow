package com.pinflow.compose

/**
 * Entry, focus, and verification motion presets for [OtpInput].
 */
sealed class OtpAnimation {
    data object None : OtpAnimation()
    data object Bounce : OtpAnimation()
    data object Pulse : OtpAnimation()
    data object Scale : OtpAnimation()
    data object Shake : OtpAnimation()
    data object SuccessWave : OtpAnimation()
}
