package com.pinflow.compose

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions

object PinFlowDefaults {

    @Composable
    fun colors(
        focusedBorderColor: Color = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor: Color = MaterialTheme.colorScheme.outlineVariant,
        errorBorderColor: Color = MaterialTheme.colorScheme.error,
        successBorderColor: Color = MaterialTheme.colorScheme.tertiary,
        focusedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
        unfocusedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        textColor: Color = MaterialTheme.colorScheme.onSurface,
        errorTextColor: Color = MaterialTheme.colorScheme.error,
        successTextColor: Color = MaterialTheme.colorScheme.tertiary,
        glowColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        cursorColor: Color = MaterialTheme.colorScheme.primary,
    ): PinFlowColors = PinFlowColors(
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        errorBorderColor = errorBorderColor,
        successBorderColor = successBorderColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        textColor = textColor,
        errorTextColor = errorTextColor,
        successTextColor = successTextColor,
        glowColor = glowColor,
        cursorColor = cursorColor,
    )

    fun dimensions(
        cellWidth: Dp = 50.dp,
        cellHeight: Dp = 64.dp,
        spacing: Dp = 12.dp,
        cornerRadius: Dp = 12.dp,
        focusedBorderWidth: Dp = 2.dp,
        unfocusedBorderWidth: Dp = 1.dp,
        cursorWidth: Dp = 2.dp,
    ): PinFlowDimensions = PinFlowDimensions(
        cellWidth = cellWidth,
        cellHeight = cellHeight,
        spacing = spacing,
        cornerRadius = cornerRadius,
        focusedBorderWidth = focusedBorderWidth,
        unfocusedBorderWidth = unfocusedBorderWidth,
        cursorWidth = cursorWidth,
    )

    fun animations(): Set<PinFlowAnimation> = setOf(
        PinFlowAnimation.Bounce,
        PinFlowAnimation.Glow,
        PinFlowAnimation.ShakeOnError,
        PinFlowAnimation.Slide,
    )

    fun keyboardOptions(
        isAlphanumeric: Boolean = false,
        secure: Boolean = false,
    ): KeyboardOptions = KeyboardOptions(
        keyboardType = when {
            isAlphanumeric -> KeyboardType.Password
            secure -> KeyboardType.NumberPassword
            else -> KeyboardType.Number
        },
        imeAction = ImeAction.Done,
    )
}

@Immutable
data class PinFlowColors(
    val focusedBorderColor: Color,
    val unfocusedBorderColor: Color,
    val errorBorderColor: Color,
    val successBorderColor: Color,
    val focusedContainerColor: Color,
    val unfocusedContainerColor: Color,
    val textColor: Color,
    val errorTextColor: Color,
    val successTextColor: Color,
    val glowColor: Color,
    val cursorColor: Color,
)

@Immutable
data class PinFlowDimensions(
    val cellWidth: Dp,
    val cellHeight: Dp,
    val spacing: Dp,
    val cornerRadius: Dp,
    val focusedBorderWidth: Dp,
    val unfocusedBorderWidth: Dp,
    val cursorWidth: Dp,
) {
    val boxShape: Shape get() = RoundedCornerShape(cornerRadius)
}
