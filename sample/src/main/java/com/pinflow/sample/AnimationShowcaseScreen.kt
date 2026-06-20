package com.pinflow.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinflow.compose.OtpAnimation
import com.pinflow.compose.OtpAnimationConfig
import com.pinflow.compose.OtpInput
import com.pinflow.compose.PinFlowValidator
import com.pinflow.compose.VerificationState
import kotlinx.coroutines.delay

@Composable
fun AnimationShowcaseScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Animation Engine",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "MVP 3 — OtpInput with motion presets and verification feedback.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        DemoSection(
            title = "Bounce",
            description = "Each digit bounces when entered. Paste still fills all cells instantly.",
        ) {
            var otp by remember { mutableStateOf("") }
            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                animation = OtpAnimation.Bounce,
                verificationState = VerificationState.Typing,
            )
        }

        DemoSection(
            title = "Pulse",
            description = "The focused cell gently pulses to guide the user.",
        ) {
            var otp by remember { mutableStateOf("") }
            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                animation = OtpAnimation.Pulse,
                verificationState = VerificationState.Typing,
            )
        }

        DemoSection(
            title = "Scale",
            description = "Cells grow slightly when filled — clean and professional.",
        ) {
            var otp by remember { mutableStateOf("") }
            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                animation = OtpAnimation.Scale,
            )
        }

        DemoSection(
            title = "Shake on error",
            description = "Enter 000000 then tap Verify to trigger a horizontal shake.",
        ) {
            var otp by remember { mutableStateOf("") }
            var state by remember { mutableStateOf<VerificationState>(VerificationState.Idle) }

            OtpInput(
                value = otp,
                onValueChange = {
                    otp = it
                    state = if (it.isEmpty()) {
                        VerificationState.Idle
                    } else {
                        VerificationState.Typing
                    }
                },
                length = 6,
                animation = OtpAnimation.Scale,
                verificationState = state,
                shakeIntensity = 12.dp,
            )

            OutlinedButton(
                onClick = {
                    state = if (otp == "000000") {
                        VerificationState.Success
                    } else {
                        VerificationState.Error("Invalid OTP")
                    }
                },
                enabled = PinFlowValidator.isComplete(otp, length = 6),
            ) {
                Text("Verify")
            }

            if (state is VerificationState.Success) {
                Text(
                    text = "Correct!",
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }

        DemoSection(
            title = "Success wave",
            description = "Enter 123456 — cells glow and scale left-to-right on success.",
        ) {
            var otp by remember { mutableStateOf("") }
            val isComplete = PinFlowValidator.isComplete(otp, length = 6)
            val state = when {
                isComplete && otp == "123456" -> VerificationState.Success
                isComplete -> VerificationState.Error("Try 123456")
                otp.isNotEmpty() -> VerificationState.Typing
                else -> VerificationState.Idle
            }

            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                animation = OtpAnimation.SuccessWave,
                verificationState = state,
                successAnimationDelay = 80,
            )
        }

        DemoSection(
            title = "Verifying",
            description = "Simulates backend verification with animated progress.",
        ) {
            var otp by remember { mutableStateOf("") }
            var state by remember { mutableStateOf<VerificationState>(VerificationState.Idle) }

            LaunchedEffect(state) {
                if (state == VerificationState.Verifying) {
                    delay(2500)
                    state = VerificationState.Success
                }
            }

            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                animation = OtpAnimation.Scale,
                verificationState = state,
                enabled = state != VerificationState.Verifying,
            )

            Button(
                onClick = { state = VerificationState.Verifying },
                enabled = PinFlowValidator.isComplete(otp, length = 6) &&
                    state != VerificationState.Verifying,
            ) {
                Text("Start verification")
            }
        }

        DemoSection(
            title = "Custom config + disabled motion",
            description = "OtpAnimationConfig tuning, or OtpAnimation.None to disable all motion.",
        ) {
            var otp by remember { mutableStateOf("") }
            var motionEnabled by remember { mutableStateOf(true) }

            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 4,
                animation = if (motionEnabled) OtpAnimation.Bounce else OtpAnimation.None,
                animationConfig = OtpAnimationConfig(
                    durationMillis = 180,
                    bounceHeight = 12.dp,
                    scaleFactor = 1.15f,
                ),
                enableHaptics = motionEnabled,
            )

            OutlinedButton(onClick = { motionEnabled = !motionEnabled }) {
                Text(if (motionEnabled) "Disable animations" else "Enable animations")
            }
        }

        Spacer(modifier = Modifier.height(64.dp))
    }
}
