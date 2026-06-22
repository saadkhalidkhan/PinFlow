package com.pinflow.compose.autofill

/**
 * Applies [OtpDetectionMode] rules and fires detection / fill callbacks.
 */
internal class OtpAutofillHandler(
    private val onOtpDetected: (String) -> Unit,
    private val onOtpFilled: (String) -> Unit,
    private val onSuggest: (String, OtpDetectionSource) -> Unit,
    private val onAutoFill: (String) -> Unit,
) {
    fun handleDetection(
        code: String,
        source: OtpDetectionSource,
        detectionMode: OtpDetectionMode,
    ) {
        if (detectionMode == OtpDetectionMode.Disabled) return
        onOtpDetected(code)
        when (detectionMode) {
            OtpDetectionMode.Disabled -> Unit
            OtpDetectionMode.AutoFill -> {
                onAutoFill(code)
                onOtpFilled(code)
            }
            OtpDetectionMode.Suggestion -> onSuggest(code, source)
        }
    }

    fun applySuggestion(code: String) {
        onAutoFill(code)
        onOtpFilled(code)
    }
}
