package com.pinflow.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Branded OTP / PIN input for Jetpack Compose — one hidden [BasicTextField], animated visual cells.
 */
@Composable
fun PinFlow(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 4,
    mode: PinFlowMode = PinFlowMode.Boxes,
    secure: Boolean = false,
    revealLastDigit: Boolean = false,
    isAlphanumeric: Boolean = false,
    animations: Set<PinFlowAnimation> = PinFlowDefaults.animations(),
    style: PinFlowStyle? = null,
    colors: PinFlowColors = PinFlowDefaults.colors(),
    dimensions: PinFlowDimensions = PinFlowDefaults.dimensions(),
    keyboardOptions: KeyboardOptions = PinFlowDefaults.keyboardOptions(
        isAlphanumeric = isAlphanumeric,
        secure = secure || mode == PinFlowMode.SecurePin,
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    isSuccess: Boolean = false,
    enabled: Boolean = true,
    maskChar: String = "\u2022",
    borderBrush: Brush? = null,
    cursorColor: Color? = null,
    cursorWidth: Dp? = null,
    cellContent: PinFlowCellContent? = null,
    hapticEnabled: Boolean = false,
    onComplete: ((String) -> Unit)? = null,
) {
    val effectiveColors = style?.colors ?: colors
    val effectiveDimensions = style?.dimensions ?: dimensions
    val effectiveBorderBrush = borderBrush ?: style?.borderBrush
    val resolvedCursorColor = cursorColor ?: style?.cursorColor ?: effectiveColors.cursorColor
    val resolvedCursorWidth = cursorWidth ?: style?.cursorWidth ?: effectiveDimensions.cursorWidth
    val haptic = LocalHapticFeedback.current
    var previousLength by remember { mutableIntStateOf(value.length) }
    val resolvedSecure = secure || mode == PinFlowMode.SecurePin
    val slotMode = when (mode) {
        PinFlowMode.SecurePin -> PinFlowMode.Circle
        PinFlowMode.SingleField -> PinFlowMode.SingleField
        else -> mode
    }

    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    var isLayoutReady by remember { mutableStateOf(false) }
    var pendingFocus by remember { mutableStateOf(false) }
    var revealedIndex by remember { mutableIntStateOf(-1) }
    val shakeOffset = remember { Animatable(0f) }
    val hiddenFieldTextStyle = TextStyle(
        color = Color.Transparent,
        fontSize = 1.sp,
    )

    val layoutDimensions = remember(effectiveDimensions, length) {
        when {
            length >= 6 -> effectiveDimensions.copy(
                cellWidth = minOf(effectiveDimensions.cellWidth, 44.dp),
                spacing = minOf(effectiveDimensions.spacing, 10.dp),
            )
            else -> effectiveDimensions
        }
    }

    val isSingleField = slotMode == PinFlowMode.SingleField
    val fieldWidthModifier = if (isSingleField) {
        Modifier.fillMaxWidth()
    } else {
        Modifier.wrapContentWidth()
    }

    LaunchedEffect(value, length) {
        if (PinFlowValidator.isComplete(value, length)) {
            onComplete?.invoke(value)
        }
    }

    LaunchedEffect(value) {
        if (hapticEnabled && value.length > previousLength) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        previousLength = value.length
    }

    LaunchedEffect(value) {
        if (revealLastDigit && resolvedSecure && value.isNotEmpty()) {
            revealedIndex = value.length - 1
            delay(500L)
            revealedIndex = -1
        } else {
            revealedIndex = -1
        }
    }

    LaunchedEffect(isLayoutReady, pendingFocus) {
        if (isLayoutReady && pendingFocus && enabled) {
            withFrameMillis { }
            focusRequester.requestFocus()
            pendingFocus = false
        }
    }

    LaunchedEffect(isError) {
        if (isError && PinFlowAnimation.ShakeOnError in animations) {
            repeat(4) {
                shakeOffset.animateTo(
                    targetValue = 10f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                )
                shakeOffset.animateTo(
                    targetValue = -10f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                )
            }
            shakeOffset.animateTo(0f)
        }
    }

    val containerModifier = modifier
        .onGloballyPositioned { coordinates ->
            if (coordinates.size.width > 0 && coordinates.size.height > 0) {
                isLayoutReady = true
            }
        }
        .semantics {
            contentDescription = "PIN input, $length characters"
        }
        .graphicsLayer { translationX = shakeOffset.value }

    @Composable
    fun PinFlowVisuals() {
        if (slotMode == PinFlowMode.SingleField) {
            SingleFieldDisplay(
                value = value,
                length = length,
                isFocused = isFocused,
                isError = isError,
                isSuccess = isSuccess,
                secure = resolvedSecure,
                revealedIndex = revealedIndex,
                maskChar = maskChar,
                colors = effectiveColors,
                dimensions = layoutDimensions,
                animations = animations,
                borderBrush = effectiveBorderBrush,
                cursorColor = resolvedCursorColor,
                cursorWidth = resolvedCursorWidth,
            )
        } else {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(layoutDimensions.spacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(length) { index ->
                    val char = value.getOrNull(index)
                    val cellState = resolveCellState(
                        char = char,
                        index = index,
                        valueLength = value.length,
                        isFocused = isFocused,
                        isError = isError,
                        isSuccess = isSuccess,
                    )
                    val shouldObscure = resolvedSecure && index != revealedIndex

                    PinFlowCell(
                        char = char,
                        cellState = cellState,
                        obscureText = shouldObscure,
                        mode = slotMode,
                        maskChar = maskChar,
                        colors = effectiveColors,
                        dimensions = layoutDimensions,
                        animations = animations,
                        borderBrush = effectiveBorderBrush,
                        cursorColor = resolvedCursorColor,
                        cursorWidth = resolvedCursorWidth,
                        cellContent = cellContent,
                    )
                }
            }
        }
    }

    Box(
        modifier = containerModifier.then(fieldWidthModifier),
        contentAlignment = Alignment.Center,
    ) {
        if (enabled && isLayoutReady) {
            BasicTextField(
                value = value,
                onValueChange = { input ->
                    val filtered = if (isAlphanumeric) {
                        input.filter { it.isLetterOrDigit() }
                    } else {
                        input.filter { it.isDigit() }
                    }
                    onValueChange(filtered.take(length))
                },
                modifier = fieldWidthModifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
                textStyle = hiddenFieldTextStyle,
                cursorBrush = SolidColor(resolvedCursorColor.copy(alpha = 0.01f)),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.Center) {
                        PinFlowVisuals()
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .then(
                                    if (slotMode == PinFlowMode.SingleField) {
                                        Modifier.fillMaxWidth()
                                    } else {
                                        Modifier
                                    },
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            innerTextField()
                        }
                    }
                },
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                PinFlowVisuals()
                if (enabled) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) { pendingFocus = true },
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleFieldDisplay(
    value: String,
    length: Int,
    isFocused: Boolean,
    isError: Boolean,
    isSuccess: Boolean,
    secure: Boolean,
    revealedIndex: Int,
    maskChar: String,
    colors: PinFlowColors,
    dimensions: PinFlowDimensions,
    animations: Set<PinFlowAnimation>,
    borderBrush: Brush?,
    cursorColor: Color,
    cursorWidth: Dp,
) {
    val display = buildString {
        for (i in 0 until length) {
            if (i > 0) append(' ')
            val c = value.getOrNull(i)
            append(
                when {
                    c == null -> ""
                    secure && i != revealedIndex -> maskChar
                    else -> c.toString()
                },
            )
        }
    }.ifEmpty { " ".repeat(length * 2 - 1).trimEnd() }

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> colors.errorBorderColor
            isSuccess -> colors.successBorderColor
            isFocused -> colors.focusedBorderColor
            else -> colors.unfocusedBorderColor
        },
        label = "singleFieldBorder",
    )

    val slideOffset = remember { Animatable(12f) }
    LaunchedEffect(value) {
        if (PinFlowAnimation.Slide in animations && value.isNotEmpty()) {
            slideOffset.snapTo(12f)
            slideOffset.animateTo(0f, tween(180, easing = LinearEasing))
        }
    }

    val borderWidth = if (isFocused) dimensions.focusedBorderWidth else dimensions.unfocusedBorderWidth

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.cellHeight)
            .graphicsLayer { translationY = slideOffset.value }
            .then(
                Modifier.pinFlowCellChrome(
                    borderWidth = borderWidth,
                    borderColor = borderColor,
                    borderBrush = borderBrush,
                    containerColor = if (isFocused) {
                        colors.focusedContainerColor
                    } else {
                        colors.unfocusedContainerColor
                    },
                    shape = dimensions.boxShape,
                    underline = false,
                ),
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (value.isEmpty() && isFocused) {
            PinFlowCursor(color = cursorColor, width = cursorWidth)
        } else {
            Text(
                text = display,
                style = MaterialTheme.typography.headlineSmall,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center,
                color = when {
                    isError -> colors.errorTextColor
                    isSuccess -> colors.successTextColor
                    else -> colors.textColor
                },
            )
        }
    }
}

private fun resolveCellState(
    char: Char?,
    index: Int,
    valueLength: Int,
    isFocused: Boolean,
    isError: Boolean,
    isSuccess: Boolean,
): PinFlowCellState = when {
    isError -> PinFlowCellState.Error
    isSuccess && char != null -> PinFlowCellState.Success
    isFocused && valueLength == index -> PinFlowCellState.Focused
    char != null -> PinFlowCellState.Filled
    else -> PinFlowCellState.Empty
}

@Composable
private fun PinFlowCursor(
    color: Color,
    width: Dp,
) {
    val transition = rememberInfiniteTransition(label = "pinFlowCursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(530),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "cursorAlpha",
    )
    Box(
        modifier = Modifier
            .width(width)
            .height(24.dp)
            .background(color.copy(alpha = alpha)),
    )
}

private fun Modifier.pinFlowCellChrome(
    borderWidth: Dp,
    borderColor: Color,
    borderBrush: Brush?,
    containerColor: Color,
    shape: Shape,
    underline: Boolean,
): Modifier = if (underline) {
    drawBehind {
        val strokeWidth = borderWidth.toPx()
        val y = size.height - strokeWidth / 2
        if (borderBrush != null) {
            drawLine(
                brush = borderBrush,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth,
            )
        } else {
            drawLine(
                color = borderColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = strokeWidth,
            )
        }
    }
} else {
    then(
        if (borderBrush != null) {
            Modifier.border(width = borderWidth, brush = borderBrush, shape = shape)
        } else {
            Modifier.border(width = borderWidth, color = borderColor, shape = shape)
        },
    ).background(color = containerColor, shape = shape)
}

@Composable
private fun PinFlowCell(
    char: Char?,
    cellState: PinFlowCellState,
    obscureText: Boolean,
    mode: PinFlowMode,
    maskChar: String,
    colors: PinFlowColors,
    dimensions: PinFlowDimensions,
    animations: Set<PinFlowAnimation>,
    borderBrush: Brush?,
    cursorColor: Color,
    cursorWidth: Dp,
    cellContent: PinFlowCellContent?,
) {
    val borderColor by animateColorAsState(
        targetValue = when (cellState) {
            PinFlowCellState.Error -> colors.errorBorderColor
            PinFlowCellState.Success -> colors.successBorderColor
            PinFlowCellState.Focused -> colors.focusedBorderColor
            else -> colors.unfocusedBorderColor
        },
        label = "cellBorder",
    )

    val borderWidth by animateDpAsState(
        targetValue = if (cellState == PinFlowCellState.Focused) {
            dimensions.focusedBorderWidth
        } else {
            dimensions.unfocusedBorderWidth
        },
        label = "cellBorderWidth",
    )

    val containerColor by animateColorAsState(
        targetValue = when (cellState) {
            PinFlowCellState.Focused -> colors.focusedContainerColor
            PinFlowCellState.Success -> colors.successBorderColor.copy(alpha = 0.12f)
            else -> colors.unfocusedContainerColor
        },
        label = "cellContainer",
    )

    val scale = remember { Animatable(1f) }
    LaunchedEffect(char) {
        if (char != null && PinFlowAnimation.Bounce in animations) {
            scale.animateTo(1.2f, tween(50, easing = LinearEasing))
            scale.animateTo(
                1f,
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            )
        }
    }

    val slideOffset = remember { Animatable(16f) }
    LaunchedEffect(char) {
        if (char != null && PinFlowAnimation.Slide in animations) {
            slideOffset.snapTo(16f)
            slideOffset.animateTo(0f, tween(160, easing = LinearEasing))
        }
    }

    val shape: Shape = when (mode) {
        PinFlowMode.Circle -> CircleShape
        PinFlowMode.Underline -> RoundedCornerShape(0.dp)
        else -> dimensions.boxShape
    }

    val glowModifier = if (
        cellState == PinFlowCellState.Focused &&
        PinFlowAnimation.Glow in animations &&
        mode != PinFlowMode.Underline
    ) {
        Modifier.shadow(
            elevation = 8.dp,
            shape = shape,
            ambientColor = colors.glowColor,
            spotColor = colors.glowColor,
        )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .widthIn(min = dimensions.cellWidth, max = dimensions.cellWidth)
            .height(dimensions.cellHeight)
            .defaultMinSize(
                minWidth = dimensions.cellWidth,
                minHeight = dimensions.cellHeight,
            )
            .graphicsLayer {
                translationY = if (PinFlowAnimation.Slide in animations) slideOffset.value else 0f
            }
            .then(glowModifier)
            .then(
                Modifier.pinFlowCellChrome(
                    borderWidth = borderWidth,
                    borderColor = borderColor,
                    borderBrush = borderBrush,
                    containerColor = containerColor,
                    shape = shape,
                    underline = mode == PinFlowMode.Underline,
                ),
            )
            .scale(scale.value),
        contentAlignment = Alignment.Center,
    ) {
        if (cellContent != null) {
            cellContent(char, cellState)
        } else {
            val textColor = when (cellState) {
                PinFlowCellState.Error -> colors.errorTextColor
                PinFlowCellState.Success -> colors.successTextColor
                else -> colors.textColor
            }

            val text = when {
                char == null -> ""
                obscureText -> maskChar
                else -> char.toString()
            }

            if (char == null && cellState == PinFlowCellState.Focused) {
                PinFlowCursor(color = cursorColor, width = cursorWidth)
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Visible,
                )
            }
        }
    }
}
