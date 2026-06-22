package com.pinflow.compose.autofill

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OtpExtractorTest {

    @Test
    fun extract_digitsOnly_findsSixDigitCode() {
        assertEquals("493721", OtpExtractor.extract("Your verification code is 493721", 6))
    }

    @Test
    fun extract_digitsOnly_findsIsolatedCode() {
        assertEquals("123456", OtpExtractor.extract("Use 123456 to sign in.", 6))
    }

    @Test
    fun extract_alphaNumeric_findsMixedCode() {
        assertEquals(
            "A7X92B",
            OtpExtractor.extract(
                text = "Promo code A7x92b expires soon",
                length = 6,
                allowedChars = OtpAllowedChars.AlphaNumeric,
            ),
        )
    }

    @Test
    fun extract_rejectsInvalidLength() {
        assertNull(OtpExtractor.extract("Code 12345", 6))
        assertNull(OtpExtractor.extract("Code 1234567", 6))
    }

    @Test
    fun extract_rejectsPartOfPhoneNumber() {
        assertNull(OtpExtractor.extract("Call us at +123456789012345", 6))
        assertNull(OtpExtractor.extract("123456789012345", 6))
    }

    @Test
    fun extract_rejectsLongMixedTransactionId() {
        assertNull(
            OtpExtractor.extract(
                text = "TXN-ABCD1234EFGH5678IJ90",
                length = 6,
                allowedChars = OtpAllowedChars.AlphaNumeric,
            ),
        )
    }

    @Test
    fun extract_prefersExactLengthOverEmbeddedNoise() {
        assertEquals(
            "902100",
            OtpExtractor.extract("OTP 902100 is your login code", 6),
        )
    }

    @Test
    fun extract_picksContextualMatchWhenMultipleCandidates() {
        val text = "Ref 111111 and verification code 222222"
        assertEquals("222222", OtpExtractor.extract(text, 6))
    }

    @Test
    fun extract_returnsNullForBlankInput() {
        assertNull(OtpExtractor.extract("", 6))
        assertNull(OtpExtractor.extract("   ", 6))
    }
}
