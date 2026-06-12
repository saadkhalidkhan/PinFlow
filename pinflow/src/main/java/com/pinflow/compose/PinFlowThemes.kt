package com.pinflow.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Preset light/dark-adaptive styles for [PinFlow].
 *
 * ```
 * val style = PinFlowThemes.Neon()
 * PinFlow(
 *     colors = style.colors,
 *     dimensions = style.dimensions,
 *     borderBrush = style.borderBrush,
 *     cursorColor = style.cursorColor,
 *     cursorWidth = style.cursorWidth,
 * )
 * ```
 */
object PinFlowThemes {

  @Composable
  fun Default(): PinFlowStyle = PinFlowStyle(
    colors = PinFlowDefaults.colors(),
    dimensions = PinFlowDefaults.dimensions(),
    borderBrush = null,
    cursorColor = MaterialTheme.colorScheme.primary,
    cursorWidth = 2.dp,
  )

  @Composable
  fun Glass(): PinFlowStyle {
    val dark = isSystemInDarkTheme()
    val scheme = MaterialTheme.colorScheme
    val gradient = Brush.linearGradient(
      colors = if (dark) {
        listOf(
          scheme.primary.copy(alpha = 0.85f),
          scheme.tertiary.copy(alpha = 0.65f),
        )
      } else {
        listOf(
          scheme.primary.copy(alpha = 0.7f),
          scheme.secondary.copy(alpha = 0.5f),
        )
      },
    )
    return PinFlowStyle(
      colors = PinFlowDefaults.colors(
        focusedBorderColor = scheme.primary.copy(alpha = if (dark) 0.9f else 0.75f),
        unfocusedBorderColor = scheme.outline.copy(alpha = if (dark) 0.35f else 0.25f),
        focusedContainerColor = scheme.surface.copy(alpha = if (dark) 0.22f else 0.45f),
        unfocusedContainerColor = scheme.surface.copy(alpha = if (dark) 0.12f else 0.28f),
        glowColor = scheme.primary.copy(alpha = if (dark) 0.45f else 0.25f),
        cursorColor = scheme.primary,
      ),
      dimensions = PinFlowDefaults.dimensions(
        cornerRadius = 16.dp,
        focusedBorderWidth = 1.5.dp,
        unfocusedBorderWidth = 1.dp,
      ),
      borderBrush = gradient,
      cursorColor = scheme.primary,
      cursorWidth = 2.dp,
    )
  }

  @Composable
  fun Neon(): PinFlowStyle {
    val dark = isSystemInDarkTheme()
    val accent = if (dark) Color(0xFF00E5FF) else Color(0xFF00B8D4)
    val accentAlt = if (dark) Color(0xFFFF00E5) else Color(0xFFD500F9)
    val gradient = Brush.linearGradient(listOf(accent, accentAlt))
    return PinFlowStyle(
      colors = PinFlowDefaults.colors(
        focusedBorderColor = accent,
        unfocusedBorderColor = accent.copy(alpha = 0.35f),
        errorBorderColor = Color(0xFFFF5252),
        successBorderColor = Color(0xFF69F0AE),
        focusedContainerColor = accent.copy(alpha = if (dark) 0.14f else 0.1f),
        unfocusedContainerColor = Color(if (dark) 0xFF121212 else 0xFFF5F5F5),
        textColor = if (dark) Color.White else Color(0xFF1A1A1A),
        errorTextColor = Color(0xFFFF5252),
        successTextColor = Color(0xFF69F0AE),
        glowColor = accent.copy(alpha = 0.55f),
        cursorColor = accent,
      ),
      dimensions = PinFlowDefaults.dimensions(
        cornerRadius = 14.dp,
        focusedBorderWidth = 2.dp,
        cellHeight = 68.dp,
      ),
      borderBrush = gradient,
      cursorColor = accent,
      cursorWidth = 3.dp,
    )
  }

  @Composable
  fun Minimal(): PinFlowStyle {
    val dark = isSystemInDarkTheme()
    val ink = if (dark) Color(0xFFE8E8E8) else Color(0xFF212121)
    val muted = ink.copy(alpha = 0.35f)
    return PinFlowStyle(
      colors = PinFlowDefaults.colors(
        focusedBorderColor = ink,
        unfocusedBorderColor = muted,
        errorBorderColor = MaterialTheme.colorScheme.error,
        successBorderColor = MaterialTheme.colorScheme.tertiary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        textColor = ink,
        glowColor = Color.Transparent,
        cursorColor = ink,
      ),
      dimensions = PinFlowDefaults.dimensions(
        cornerRadius = 8.dp,
        focusedBorderWidth = 1.5.dp,
        unfocusedBorderWidth = 1.dp,
        cellWidth = 46.dp,
        cellHeight = 56.dp,
        spacing = 10.dp,
      ),
      borderBrush = null,
      cursorColor = ink,
      cursorWidth = 1.5.dp,
    )
  }
}

@Immutable
data class PinFlowStyle(
  val colors: PinFlowColors,
  val dimensions: PinFlowDimensions,
  val borderBrush: Brush? = null,
  val cursorColor: Color,
  val cursorWidth: Dp,
)
