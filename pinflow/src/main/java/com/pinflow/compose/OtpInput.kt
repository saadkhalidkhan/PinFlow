package com.pinflow.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Animated OTP input with verification-state feedback — the MVP 3 animation API.
 *
 * Wraps [PinFlow] with [OtpAnimation] presets, [VerificationState] handling, and a verifying
 * progress indicator. Existing [PinFlow] callers remain unchanged.
 */
@Composable
fun OtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    animation: OtpAnimation = OtpAnimation.Scale,
    verificationState: VerificationState = VerificationState.Idle,
    animationDuration: Int = 250,
    successAnimationDelay: Int = 80,
    shakeIntensity: Dp = 12.dp,
    enableHaptics: Boolean = true,
    animationConfig: OtpAnimationConfig = OtpAnimationConfig(),
    mode: PinFlowMode = PinFlowMode.Boxes,
    colors: PinFlowColors = PinFlowDefaults.colors(),
    dimensions: PinFlowDimensions = PinFlowDefaults.dimensions(),
    enabled: Boolean = true,
    onComplete: ((String) -> Unit)? = null,
) {
    val resolvedConfig = animationConfig.copy(
        durationMillis = animationDuration,
        shakeDistance = shakeIntensity,
        waveDelayMillis = successAnimationDelay,
    )

    val isError = verificationState is VerificationState.Error
    val isSuccess = verificationState is VerificationState.Success
    val errorMessage = (verificationState as? VerificationState.Error)?.message

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PinFlow(
            value = value,
            onValueChange = onValueChange,
            length = length,
            mode = mode,
            colors = colors,
            dimensions = dimensions,
            enabled = enabled,
            isError = isError,
            isSuccess = isSuccess,
            hapticEnabled = enableHaptics,
            otpAnimation = animation,
            animationConfig = resolvedConfig,
            pulseWhenFocused = verificationState is VerificationState.Typing ||
                verificationState is VerificationState.Idle,
            onComplete = onComplete,
        )

        when (verificationState) {
            VerificationState.Verifying -> {
                OtpVerifyingIndicator(
                    length = length,
                    dimensions = dimensions,
                )
            }
            is VerificationState.Error -> {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = colors.errorTextColor,
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            else -> Unit
        }
    }
}

@Composable
private fun OtpVerifyingIndicator(
    length: Int,
    dimensions: PinFlowDimensions,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val transition = rememberInfiniteTransition(label = "otpVerifying")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "verifyingProgress",
    )

    val totalWidth = (dimensions.cellWidth * length) + (dimensions.spacing * (length - 1))

    Box(
        modifier = modifier
            .width(totalWidth)
            .height(12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.2f)),
        )
        Box(
            modifier = Modifier
                .width(totalWidth * 0.35f)
                .height(2.dp)
                .offset(x = totalWidth * 0.65f * progress)
                .clip(CircleShape)
                .background(primary),
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val dotAlpha by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "verifyingDot$index",
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer { alpha = dotAlpha }
                    .clip(CircleShape)
                    .background(primary),
            )
        }
    }
}
