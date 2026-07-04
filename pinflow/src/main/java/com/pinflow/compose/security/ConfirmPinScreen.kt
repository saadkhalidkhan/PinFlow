package com.pinflow.compose.security

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/**
 * Screen for confirming a previously created PIN.
 *
 * Compares the entered PIN with [originalPin] in memory. Does not store or log PIN values.
 */
@Composable
fun ConfirmPinScreen(
    originalPin: String,
    onPinConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
    pinLength: Int = originalPin.length,
    title: String = "Confirm PIN",
    subtitle: String? = "Re-enter your PIN to confirm",
    secureScreen: Boolean = false,
    onPinMismatch: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
) {
    SecureScreenEffect(secureScreen)

    var pin by remember { mutableStateOf("") }
    var isMismatch by remember { mutableStateOf(false) }

    LaunchedEffect(isMismatch) {
        if (isMismatch) {
            pin = ""
        }
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
        if (headerContent != null) {
            headerContent()
        }

        PinInput(
            value = pin,
            onValueChange = { newValue ->
                pin = newValue
                isMismatch = false
            },
            length = pinLength,
            isError = isMismatch,
            onComplete = { confirmedPin ->
                if (confirmedPin == originalPin) {
                    pin = ""
                    onPinConfirmed()
                } else {
                    isMismatch = true
                    onPinMismatch?.invoke()
                }
            },
        )

        if (isMismatch) {
            Text(
                text = "PINs do not match. Try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
