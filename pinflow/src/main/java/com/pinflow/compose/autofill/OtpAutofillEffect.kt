package com.pinflow.compose.autofill

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

internal data class OtpSuggestionState(
    val code: String,
    val source: OtpDetectionSource,
)

internal class OtpAutofillBindings(
    val suggestion: OtpSuggestionState?,
    val onFocusChanged: (Boolean) -> Unit,
    val applySuggestion: () -> Unit,
    val dismissSuggestion: () -> Unit,
)

@Composable
internal fun rememberOtpAutofillBindings(
    length: Int,
    allowedChars: OtpAllowedChars,
    smsAutoFill: Boolean,
    clipboardSuggestion: Boolean,
    otpDetectionMode: OtpDetectionMode,
    currentValue: String,
    onValueChange: (String) -> Unit,
    onOtpDetected: (String) -> Unit,
    onOtpFilled: (String) -> Unit,
): OtpAutofillBindings {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val clipboardDetector = remember { ClipboardOtpDetector() }
    var suggestion by remember { mutableStateOf<OtpSuggestionState?>(null) }

    val latestValue by rememberUpdatedState(currentValue)
    val latestOnValueChange by rememberUpdatedState(onValueChange)
    val latestOnOtpDetected by rememberUpdatedState(onOtpDetected)
    val latestOnOtpFilled by rememberUpdatedState(onOtpFilled)
    val latestDetectionMode by rememberUpdatedState(otpDetectionMode)
    val latestLength by rememberUpdatedState(length)
    val latestAllowedChars by rememberUpdatedState(allowedChars)
    val latestClipboardEnabled by rememberUpdatedState(clipboardSuggestion)

    val handler = remember {
        OtpAutofillHandler(
            onOtpDetected = { code -> latestOnOtpDetected(code) },
            onOtpFilled = { code -> latestOnOtpFilled(code) },
            onSuggest = { code, source -> suggestion = OtpSuggestionState(code, source) },
            onAutoFill = { code -> latestOnValueChange(code) },
        )
    }

    val processDetection: (String, OtpDetectionSource) -> Unit = { code, source ->
        if (latestValue.isEmpty()) {
            handler.handleDetection(code, source, latestDetectionMode)
        }
    }
    val latestProcessDetection by rememberUpdatedState(processDetection)

    val readClipboard: () -> String? = {
        runCatching {
            val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            manager?.primaryClip?.getItemAt(0)?.text?.toString()
        }.getOrNull()
    }
    val latestReadClipboard by rememberUpdatedState(readClipboard)

    val checkClipboard: () -> Unit = {
        if (
            latestClipboardEnabled &&
            !isPreview &&
            latestDetectionMode != OtpDetectionMode.Disabled
        ) {
            val code = clipboardDetector.detect(
                clipboardText = latestReadClipboard(),
                length = latestLength,
                allowedChars = latestAllowedChars,
            )
            if (code != null) {
                latestProcessDetection(code, OtpDetectionSource.Clipboard)
            }
        }
    }
    val latestCheckClipboard by rememberUpdatedState(checkClipboard)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, clipboardSuggestion, isPreview) {
        if (!clipboardSuggestion || isPreview) return@DisposableEffect onDispose {}

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                latestCheckClipboard()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val onSmsMessage: (String) -> Unit = { message ->
        val code = OtpExtractor.extract(message, latestLength, latestAllowedChars)
        if (code != null) {
            latestProcessDetection(code, OtpDetectionSource.Sms)
        }
    }
    val latestOnSmsMessage by rememberUpdatedState(onSmsMessage)

    DisposableEffect(smsAutoFill, isPreview) {
        if (!smsAutoFill || isPreview) return@DisposableEffect onDispose {}

        val retriever = SmsOtpRetriever(context) { message -> latestOnSmsMessage(message) }
        retriever.start()
        onDispose { retriever.stop() }
    }

    LaunchedEffect(currentValue) {
        if (currentValue.isNotEmpty()) {
            suggestion = null
        }
    }

    return OtpAutofillBindings(
        suggestion = suggestion,
        onFocusChanged = { focused ->
            if (focused) latestCheckClipboard()
        },
        applySuggestion = {
            val current = suggestion ?: return@OtpAutofillBindings
            handler.applySuggestion(current.code)
            clipboardDetector.markHandled(current.code)
            suggestion = null
        },
        dismissSuggestion = {
            val current = suggestion ?: return@OtpAutofillBindings
            clipboardDetector.markHandled(current.code)
            suggestion = null
        },
    )
}
