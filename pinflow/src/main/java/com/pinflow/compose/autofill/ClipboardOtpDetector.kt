package com.pinflow.compose.autofill

/**
 * Detects OTP codes from clipboard text without polling.
 *
 * Tracks the last clipboard snapshot and handled code so the same suggestion is not shown
 * repeatedly until the clipboard content changes.
 */
class ClipboardOtpDetector {

    private var lastClipboardText: String? = null
    private var lastHandledCode: String? = null
    private var lastSuggestedCode: String? = null

    /**
     * Returns a valid OTP extracted from [clipboardText], or `null` when nothing should be suggested.
     */
    fun detect(
        clipboardText: String?,
        length: Int,
        allowedChars: OtpAllowedChars = OtpAllowedChars.DigitsOnly,
    ): String? {
        val text = clipboardText?.trim().orEmpty()
        if (text.isEmpty()) return null

        if (text != lastClipboardText) {
            lastClipboardText = text
            lastHandledCode = null
            lastSuggestedCode = null
        }

        val code = OtpExtractor.extract(text, length, allowedChars) ?: return null
        if (code == lastHandledCode || code == lastSuggestedCode) return null
        lastSuggestedCode = code
        return code
    }

    /** Call when the user applies or dismisses a suggestion so it is not shown again. */
    fun markHandled(code: String) {
        lastHandledCode = code
    }

    /** Clears tracking state — useful in tests. */
    fun reset() {
        lastClipboardText = null
        lastHandledCode = null
        lastSuggestedCode = null
    }
}
