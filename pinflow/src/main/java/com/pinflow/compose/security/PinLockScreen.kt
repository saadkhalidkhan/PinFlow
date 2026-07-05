package com.pinflow.compose.security

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity

/**
 * Reusable app lock screen for PIN entry.
 *
 * Verification is the caller's responsibility — pass [onPinEntered] and validate against your
 * stored hash. Optionally supports biometric unlock via [BiometricAuthenticator].
 *
 * Requires `androidx.biometric:biometric` when [biometricEnabled] is true.
 */
@Composable
fun PinLockScreen(
    onPinEntered: (String) -> Unit,
    modifier: Modifier = Modifier,
    pinLength: Int = 6,
    title: String = "Enter PIN",
    subtitle: String? = "Unlock your app",
    isVerifying: Boolean = false,
    isError: Boolean = false,
    attemptsRemaining: Int? = null,
    biometricEnabled: Boolean = false,
    biometricPromptTitle: String = "Unlock",
    biometricPromptSubtitle: String? = null,
    secureScreen: Boolean = false,
    onBiometricSuccess: (() -> Unit)? = null,
    onBiometricError: ((BiometricAuthResult) -> Unit)? = null,
    onForgotPin: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
) {
    SecureScreenEffect(secureScreen)

    var pin by remember { mutableStateOf("") }
    val biometricAvailability = rememberBiometricAvailability()
    val showBiometric = biometricEnabled &&
        biometricAvailability == BiometricAvailability.Available &&
        onBiometricSuccess != null
    val isPreview = LocalInspectionMode.current

    LaunchedEffect(isError) {
        if (isError) {
            pin = ""
        }
    }

    PinSecurityScreenLayout(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        onCancel = onCancel,
    ) {
        if (headerContent != null) {
            headerContent()
        }

        if (isVerifying) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
            )
        }

        PinInput(
            value = pin,
            onValueChange = { pin = it },
            length = pinLength,
            isError = isError,
            enabled = !isVerifying,
            onComplete = { enteredPin ->
                onPinEntered(enteredPin)
            },
        )

        if (isError) {
            Text(
                text = "Incorrect PIN. Try again.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (attemptsRemaining != null) {
            Text(
                text = when (attemptsRemaining) {
                    0 -> "No attempts remaining"
                    1 -> "1 attempt remaining"
                    else -> "$attemptsRemaining attempts remaining"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }

        if (showBiometric) {
            val context = LocalContext.current
            BiometricAuthButton(
                onClick = {
                    val activity = context as? FragmentActivity
                    if (activity == null || isPreview) {
                        onBiometricError?.invoke(
                            BiometricAuthResult.Error(-1, "Biometric authentication unavailable"),
                        )
                        return@BiometricAuthButton
                    }
                    BiometricAuthenticator.authenticate(
                        activity = activity,
                        title = biometricPromptTitle,
                        subtitle = biometricPromptSubtitle,
                        onResult = { result ->
                            when (result) {
                                BiometricAuthResult.Success -> onBiometricSuccess?.invoke()
                                BiometricAuthResult.Failed -> Unit
                                BiometricAuthResult.Cancelled -> Unit
                                is BiometricAuthResult.Error -> onBiometricError?.invoke(result)
                            }
                        },
                    )
                },
                enabled = !isVerifying,
                availability = biometricAvailability,
                modifier = Modifier.padding(top = 16.dp),
            )
        }

        if (onForgotPin != null) {
            TextButton(
                onClick = onForgotPin,
                enabled = !isVerifying,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Forgot PIN?")
            }
        }
    }
}
