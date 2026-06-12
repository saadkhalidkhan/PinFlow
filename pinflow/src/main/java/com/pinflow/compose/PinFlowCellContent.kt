package com.pinflow.compose

import androidx.compose.runtime.Composable

/**
 * Custom renderer for a single OTP cell. Replaces the default digit [androidx.compose.material3.Text]
 * while keeping cell chrome (border, background, animations) unless you fully customize via layout.
 *
 * ```
 * cellContent = { digit, state ->
 *     Text(
 *         text = digit?.toString() ?: "—",
 *         color = if (state == PinFlowCellState.Error) Color.Red else Color.White,
 *     )
 * }
 * ```
 */
typealias PinFlowCellContent = @Composable (digit: Char?, state: PinFlowCellState) -> Unit
