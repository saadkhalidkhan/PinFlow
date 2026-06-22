package com.pinflow.compose.autofill

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OtpAutofillHandlerTest {

    @Test
    fun autoFillMode_fillsImmediatelyAndFiresCallbacks() {
        var detected = ""
        var filled = ""
        var suggested = false
        var value = ""

        val handler = OtpAutofillHandler(
            onOtpDetected = { detected = it },
            onOtpFilled = { filled = it },
            onSuggest = { _, _ -> suggested = true },
            onAutoFill = { value = it },
        )

        handler.handleDetection("493721", OtpDetectionSource.Sms, OtpDetectionMode.AutoFill)

        assertEquals("493721", detected)
        assertEquals("493721", filled)
        assertEquals("493721", value)
        assertFalse(suggested)
    }

    @Test
    fun suggestionMode_onlyDetectsWithoutFilling() {
        var detected = ""
        var filled = ""
        var suggestedCode = ""
        var value = ""

        val handler = OtpAutofillHandler(
            onOtpDetected = { detected = it },
            onOtpFilled = { filled = it },
            onSuggest = { code, _ -> suggestedCode = code },
            onAutoFill = { value = it },
        )

        handler.handleDetection("493721", OtpDetectionSource.Clipboard, OtpDetectionMode.Suggestion)

        assertEquals("493721", detected)
        assertEquals("493721", suggestedCode)
        assertEquals("", filled)
        assertEquals("", value)
    }

    @Test
    fun applySuggestion_fillsAndFiresOnOtpFilled() {
        var filled = ""
        var value = ""

        val handler = OtpAutofillHandler(
            onOtpDetected = {},
            onOtpFilled = { filled = it },
            onSuggest = { _, _ -> },
            onAutoFill = { value = it },
        )

        handler.applySuggestion("493721")

        assertEquals("493721", filled)
        assertEquals("493721", value)
    }

    @Test
    fun disabledMode_ignoresDetection() {
        var detected = ""
        var filled = ""
        var suggested = false

        val handler = OtpAutofillHandler(
            onOtpDetected = { detected = it },
            onOtpFilled = { filled = it },
            onSuggest = { _, _ -> suggested = true },
            onAutoFill = {},
        )

        handler.handleDetection("493721", OtpDetectionSource.Sms, OtpDetectionMode.Disabled)

        assertEquals("", detected)
        assertEquals("", filled)
        assertFalse(suggested)
    }
}
