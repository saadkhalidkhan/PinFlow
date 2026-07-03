package com.pinflow.compose.security

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Button that launches biometric authentication when tapped.
 *
 * Hidden automatically when biometrics are unavailable. Safe to use in Preview mode.
 */
@Composable
fun BiometricAuthButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    availability: BiometricAvailability = BiometricAvailability.Available,
    useOutlinedStyle: Boolean = true,
    label: String = "Use biometrics",
) {
    if (LocalInspectionMode.current) return
    if (availability != BiometricAvailability.Available) return

    val buttonModifier = modifier
        .fillMaxWidth()
        .semantics { contentDescription = label }

    if (useOutlinedStyle) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
        ) {
            Text(label)
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
        ) {
            Text(label)
        }
    }
}

@Composable
internal fun rememberBiometricAvailability(): BiometricAvailability {
    if (LocalInspectionMode.current) return BiometricAvailability.Unavailable
    val context = androidx.compose.ui.platform.LocalContext.current
    return androidx.compose.runtime.remember(context) {
        BiometricAuthenticator.checkAvailability(context)
    }
}
