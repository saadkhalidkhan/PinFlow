package com.pinflow.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pinflow.compose.security.BiometricAuthResult
import com.pinflow.compose.security.CreatePinScreen
import com.pinflow.compose.security.PinLockScreen
import com.pinflow.compose.security.PinSetupScreen
import com.pinflow.compose.security.PinStrength

@Composable
fun SecurityShowcaseScreen(modifier: Modifier = Modifier) {
    var section by remember { mutableStateOf(SecuritySection.Setup) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "PIN & Security Suite",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "One library for OTP, PIN creation, and app locking. " +
                "PinFlow collects input only — hash and store PINs in your app.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SecuritySection.entries.forEach { item ->
                FilterChip(
                    selected = section == item,
                    onClick = { section = item },
                    label = { Text(item.label) },
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }

        when (section) {
            SecuritySection.Setup -> PinSetupDemo()
            SecuritySection.Create -> CreatePinDemo()
            SecuritySection.Lock -> PinLockDemo()
        }

        Spacer(modifier = Modifier.height(64.dp))
    }
}

private enum class SecuritySection(val label: String) {
    Setup("Full setup"),
    Create("Create PIN"),
    Lock("App lock"),
}

@Composable
private fun PinSetupDemo() {
    var status by remember { mutableStateOf("Not set up") }

    DemoSection(
        title = "PinSetupScreen",
        description = "Create + confirm in one flow. Strength validation blocks weak PINs.",
    ) {
        PinSetupScreen(
            pinLength = 6,
            minStrength = PinStrength.Medium,
            secureScreen = false,
            onPinSetupComplete = { _ ->
                status = "PIN setup complete (hash & store in your app)"
            },
            onCancel = { status = "Setup cancelled" },
        )
        StatusLine("Status", status)
    }
}

@Composable
private fun CreatePinDemo() {
    var status by remember { mutableStateOf("Waiting for PIN") }

    DemoSection(
        title = "CreatePinScreen",
        description = "Live strength feedback while typing. Weak PINs are rejected.",
    ) {
        CreatePinScreen(
            pinLength = 6,
            minStrength = PinStrength.Medium,
            onPinCreated = { _ ->
                status = "PIN created (pass to ConfirmPinScreen or hash now)"
            },
            onCancel = { status = "Cancelled" },
        )
        StatusLine("Status", status)
    }
}

@Composable
private fun PinLockDemo() {
    val demoPin = remember { "482917" }
    var isError by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var attempts by remember { mutableIntStateOf(3) }
    var status by remember { mutableStateOf("Locked") }

    DemoSection(
        title = "PinLockScreen",
        description = "Demo PIN is 482917. Biometric button shows when hardware is available.",
    ) {
        PinLockScreen(
            pinLength = 6,
            isError = isError,
            isVerifying = isVerifying,
            attemptsRemaining = attempts,
            biometricEnabled = true,
            biometricPromptTitle = "Unlock PinFlow demo",
            biometricPromptSubtitle = "Use fingerprint or face unlock",
            onPinEntered = { entered ->
                isVerifying = true
                isError = false
                // Simulate async verification
                entered.let { pin ->
                    if (pin == demoPin) {
                        status = "Unlocked"
                        isVerifying = false
                    } else {
                        attempts = (attempts - 1).coerceAtLeast(0)
                        isError = true
                        isVerifying = false
                        status = "Incorrect PIN"
                    }
                }
            },
            onBiometricSuccess = { status = "Unlocked via biometrics" },
            onBiometricError = { result ->
                if (result is BiometricAuthResult.Error) {
                    status = "Biometric error"
                }
            },
            onForgotPin = { status = "Forgot PIN tapped" },
        )
        StatusLine("Status", status)
        StatusLine("Hint", "Try PIN 482917")
    }
}

@Composable
private fun StatusLine(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
        textAlign = TextAlign.Center,
    )
}
