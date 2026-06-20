package com.pinflow.compose

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class OtpAnimationConfig(
    val durationMillis: Int = 250,
    val bounceHeight: Dp = 8.dp,
    val scaleFactor: Float = 1.12f,
    val shakeDistance: Dp = 10.dp,
    val waveDelayMillis: Int = 70,
)
