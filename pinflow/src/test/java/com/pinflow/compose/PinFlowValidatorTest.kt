package com.pinflow.compose

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinFlowValidatorTest {

    @Test
    fun isComplete_whenLengthMatches_returnsTrue() {
        assertTrue(PinFlowValidator.isComplete("1234", 4))
        assertFalse(PinFlowValidator.isComplete("123", 4))
    }

    @Test
    fun isNumeric_acceptsDigitsOnly() {
        assertTrue(PinFlowValidator.isNumeric("90210"))
        assertFalse(PinFlowValidator.isNumeric("90a"))
        assertFalse(PinFlowValidator.isNumeric(""))
    }

    @Test
    fun hasRepeatedDigits_detectsAllSame() {
        assertTrue(PinFlowValidator.hasRepeatedDigits("1111"))
        assertFalse(PinFlowValidator.hasRepeatedDigits("1212"))
        assertFalse(PinFlowValidator.hasRepeatedDigits("1"))
    }
}
