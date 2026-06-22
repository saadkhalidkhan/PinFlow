package com.pinflow.compose.autofill

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ClipboardOtpDetectorTest {

    private lateinit var detector: ClipboardOtpDetector

    @Before
    fun setUp() {
        detector = ClipboardOtpDetector()
    }

    @Test
    fun detect_returnsValidOtpFromClipboard() {
        assertEquals("493721", detector.detect("493721", length = 6))
    }

    @Test
    fun detect_rejectsInvalidLength() {
        assertNull(detector.detect("12345", length = 6))
    }

    @Test
    fun detect_doesNotRepeatSameSuggestionUntilClipboardChanges() {
        assertEquals("493721", detector.detect("Your code 493721", length = 6))
        assertNull(detector.detect("Your code 493721", length = 6))
    }

    @Test
    fun detect_showsAgainWhenClipboardChanges() {
        assertEquals("493721", detector.detect("493721", length = 6))
        detector.markHandled("493721")
        assertEquals("112233", detector.detect("112233", length = 6))
    }

    @Test
    fun markHandled_suppressesUntilClipboardChanges() {
        assertEquals("493721", detector.detect("493721", length = 6))
        detector.markHandled("493721")
        assertNull(detector.detect("493721", length = 6))
        assertEquals("493721", detector.detect("new 493721 text", length = 6))
    }

    @Test
    fun detect_supportsAlphanumericCodes() {
        assertEquals(
            "A7X92B",
            detector.detect(
                clipboardText = "Copy A7x92b now",
                length = 6,
                allowedChars = OtpAllowedChars.AlphaNumeric,
            ),
        )
    }
}
