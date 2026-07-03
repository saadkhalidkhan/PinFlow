package com.pinflow.compose.security

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

internal fun PinWeaknessReason.toMessage(): String = when (this) {
    PinWeaknessReason.TooShort -> "PIN is too short"
    PinWeaknessReason.RepeatedDigits -> "Avoid using the same digit repeatedly"
    PinWeaknessReason.SequentialAscending -> "Avoid sequential PINs"
    PinWeaknessReason.SequentialDescending -> "Avoid sequential PINs"
    PinWeaknessReason.CommonPin -> "This PIN is too common"
}

internal fun PinStrength.toLabel(): String = when (this) {
    PinStrength.Weak -> "Weak"
    PinStrength.Medium -> "Medium"
    PinStrength.Strong -> "Strong"
}

@Composable
internal fun PinStrengthIndicator(
    strength: PinStrength,
    modifier: Modifier = Modifier,
) {
    val color = when (strength) {
        PinStrength.Weak -> MaterialTheme.colorScheme.error
        PinStrength.Medium -> MaterialTheme.colorScheme.tertiary
        PinStrength.Strong -> MaterialTheme.colorScheme.primary
    }
    Text(
        text = "PIN strength: ${strength.toLabel()}",
        style = MaterialTheme.typography.labelMedium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "PIN strength ${strength.toLabel()}" },
    )
}

@Composable
internal fun PinSecurityScreenLayout(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null,
    headerContent: @Composable (ColumnScope.() -> Unit)? = null,
    footerContent: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { contentDescription = title },
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            )
        }
        headerContent?.invoke(this)
        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.padding(vertical = 24.dp),
        )
        content()
        footerContent?.invoke(this)
        if (onCancel != null) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .semantics { contentDescription = "Cancel" },
            ) {
                Text("Cancel")
            }
        }
    }
}
