package com.pinflow.compose.security

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/**
 * Screen for creating a new PIN.
 *
 * The library collects PIN input only — hash, encrypt, and store the PIN securely in your app.
 * PIN state is held in memory and cleared after completion or cancellation.
 */
@Composable
fun CreatePinScreen(
    onPinCreated: (String) -> Unit,
    modifier: Modifier = Modifier,
    pinLength: Int = 6,
    minStrength: PinStrength = PinStrength.Medium,
    title: String = "Create PIN",
    subtitle: String? = "Choose a secure PIN to protect your account",
    secureScreen: Boolean = false,
    onCancel: (() -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
) {
    SecureScreenEffect(secureScreen)

    var pin by remember { mutableStateOf("") }
    var showWeaknessError by remember { mutableStateOf(false) }
    var weaknessReason by remember { mutableStateOf<PinWeaknessReason?>(null) }

    val liveStrength = if (pin.isNotEmpty()) {
        PinStrengthValidator.evaluate(pin)
    } else {
        null
    }

    PinSecurityScreenLayout(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onCancel = {
            pin = ""
            onCancel?.invoke()
        },
    ) {
        headerContent?.invoke()

        PinInput(
            value = pin,
            onValueChange = { newValue ->
                pin = newValue
                showWeaknessError = false
                weaknessReason = null
            },
            length = pinLength,
            isError = showWeaknessError,
            onComplete = { completedPin ->
                val result = PinStrengthValidator.validate(completedPin, minStrength)
                if (result.isValid) {
                    onPinCreated(completedPin)
                    pin = ""
                } else {
                    weaknessReason = result.reason
                    showWeaknessError = true
                    pin = ""
                }
            },
        )

        if (liveStrength != null && pin.length < pinLength) {
            PinStrengthIndicator(
                strength = liveStrength,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (showWeaknessError && weaknessReason != null) {
            Text(
                text = weaknessReason!!.toMessage(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
