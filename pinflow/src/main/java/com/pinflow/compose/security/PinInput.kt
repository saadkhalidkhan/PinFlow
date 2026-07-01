package com.pinflow.compose.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pinflow.compose.PinFlow
import com.pinflow.compose.PinFlowDefaults
import com.pinflow.compose.PinFlowMode
import com.pinflow.compose.PinFlowColors
import com.pinflow.compose.PinFlowDimensions

/**
 * Secure PIN input built on [PinFlow] with masking enabled by default.
 *
 * PIN state is held by the caller — this composable does not store or persist values.
 */
@Composable
fun PinInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    isError: Boolean = false,
    isSuccess: Boolean = false,
    enabled: Boolean = true,
    revealLastDigit: Boolean = false,
    colors: PinFlowColors = PinFlowDefaults.colors(),
    dimensions: PinFlowDimensions = PinFlowDefaults.dimensions(),
    hapticEnabled: Boolean = true,
    onComplete: ((String) -> Unit)? = null,
) {
    PinFlow(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        length = length,
        mode = PinFlowMode.SecurePin,
        revealLastDigit = revealLastDigit,
        colors = colors,
        dimensions = dimensions,
        isError = isError,
        isSuccess = isSuccess,
        enabled = enabled,
        hapticEnabled = hapticEnabled,
        onComplete = onComplete,
    )
}
