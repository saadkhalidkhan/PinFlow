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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinflow.compose.OtpInput
import com.pinflow.compose.VerificationState
import com.pinflow.compose.autofill.OtpDetectionMode

@Composable
fun AutofillShowcaseScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Autofill & Platform Intelligence",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Users rarely need to type OTPs manually — PinFlow Compose detects, suggests, and fills codes intelligently.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        DemoSection(
            title = "SMS + clipboard suggestions",
            description = "Focus the field or resume the app after copying a 6-digit code. SMS Retriever listens without READ_SMS.",
        ) {
            var otp by remember { mutableStateOf("") }
            var detected by remember { mutableStateOf("") }
            var filled by remember { mutableStateOf("") }

            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                smsAutoFill = true,
                clipboardSuggestion = true,
                otpDetectionMode = OtpDetectionMode.Suggestion,
                verificationState = VerificationState.Typing,
                onOtpDetected = { detected = it },
                onOtpFilled = { filled = it },
            )

            StatusLine("Detected", detected)
            StatusLine("Filled", filled)
            StatusLine("Current", otp)
        }

        DemoSection(
            title = "Instant autofill mode",
            description = "otpDetectionMode = AutoFill inserts the code immediately when SMS or clipboard matches.",
        ) {
            var otp by remember { mutableStateOf("") }
            var mode by remember { mutableStateOf<OtpDetectionMode>(OtpDetectionMode.AutoFill) }

            RowChips(
                selected = mode,
                onSelect = { mode = it },
            )

            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                clipboardSuggestion = true,
                otpDetectionMode = mode,
                verificationState = VerificationState.Typing,
            )
            StatusLine("Current", otp)
        }

        DemoSection(
            title = "Clipboard only",
            description = "Copy a code like 493721, then focus the input or return to the app.",
        ) {
            var otp by remember { mutableStateOf("") }

            OtpInput(
                value = otp,
                onValueChange = { otp = it },
                length = 6,
                clipboardSuggestion = true,
                otpDetectionMode = OtpDetectionMode.Suggestion,
                verificationState = VerificationState.Typing,
            )
        }

        Spacer(modifier = Modifier.height(64.dp))
    }
}

@Composable
private fun StatusLine(label: String, value: String) {
    Text(
        text = "$label: ${value.ifEmpty { "—" }}",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun RowChips(
    selected: OtpDetectionMode,
    onSelect: (OtpDetectionMode) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selected is OtpDetectionMode.AutoFill,
            onClick = { onSelect(OtpDetectionMode.AutoFill) },
            label = { Text("AutoFill") },
        )
        FilterChip(
            selected = selected is OtpDetectionMode.Suggestion,
            onClick = { onSelect(OtpDetectionMode.Suggestion) },
            label = { Text("Suggestion") },
        )
        FilterChip(
            selected = selected is OtpDetectionMode.Disabled,
            onClick = { onSelect(OtpDetectionMode.Disabled) },
            label = { Text("Disabled") },
        )
    }
}
