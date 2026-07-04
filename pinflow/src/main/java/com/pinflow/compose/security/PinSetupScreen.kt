package com.pinflow.compose.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

private enum class PinSetupStep {
    Create,
    Confirm,
}

/**
 * Combined create-and-confirm PIN setup flow.
 *
 * Returns the final raw PIN to [onPinSetupComplete]. Your app must hash and store it securely —
 * this library never persists PINs.
 */
@Composable
fun PinSetupScreen(
    onPinSetupComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    pinLength: Int = 6,
    minStrength: PinStrength = PinStrength.Medium,
    createTitle: String = "Create PIN",
    createSubtitle: String? = "Choose a secure PIN to protect your account",
    confirmTitle: String = "Confirm PIN",
    confirmSubtitle: String? = "Re-enter your PIN to confirm",
    secureScreen: Boolean = false,
    onCancel: (() -> Unit)? = null,
) {
    var step by remember { mutableStateOf(PinSetupStep.Create) }
    var createdPin by remember { mutableStateOf("") }

    when (step) {
        PinSetupStep.Create -> CreatePinScreen(
            pinLength = pinLength,
            minStrength = minStrength,
            title = createTitle,
            subtitle = createSubtitle,
            secureScreen = secureScreen,
            modifier = modifier,
            onPinCreated = { pin ->
                createdPin = pin
                step = PinSetupStep.Confirm
            },
            onCancel = {
                createdPin = ""
                onCancel?.invoke()
            },
        )

        PinSetupStep.Confirm -> ConfirmPinScreen(
            originalPin = createdPin,
            pinLength = pinLength,
            title = confirmTitle,
            subtitle = confirmSubtitle,
            secureScreen = secureScreen,
            modifier = modifier,
            onPinConfirmed = {
                val finalPin = createdPin
                createdPin = ""
                onPinSetupComplete(finalPin)
            },
            onPinMismatch = {
                // Stay on confirm step; ConfirmPinScreen shows error and clears input.
            },
            onCancel = {
                createdPin = ""
                step = PinSetupStep.Create
                onCancel?.invoke()
            },
        )
    }
}
