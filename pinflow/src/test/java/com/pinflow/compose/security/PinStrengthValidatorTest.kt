package com.pinflow.compose.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PinStrengthValidatorTest {

    @Test
    fun evaluate_repeatedDigits_isWeak() {
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("111111"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("222222"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("000000"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("1111"))
    }

    @Test
    fun evaluate_ascendingSequences_isWeak() {
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("123456"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("012345"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("234567"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("1234"))
    }

    @Test
    fun evaluate_descendingSequences_isWeak() {
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("654321"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("987654"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("4321"))
    }

    @Test
    fun evaluate_commonPins_isWeak() {
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("1234"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("0000"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("1111"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("1212"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("7777"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("2580"))
    }

    @Test
    fun evaluate_randomFourDigit_isMedium() {
        assertEquals(PinStrength.Medium, PinStrengthValidator.evaluate("4829"))
        assertEquals(PinStrength.Medium, PinStrengthValidator.evaluate("5913"))
    }

    @Test
    fun evaluate_randomSixDigit_isStrong() {
        assertEquals(PinStrength.Strong, PinStrengthValidator.evaluate("482917"))
        assertEquals(PinStrength.Strong, PinStrengthValidator.evaluate("591304"))
    }

    @Test
    fun evaluate_tooShort_isWeak() {
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate(""))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("12"))
        assertEquals(PinStrength.Weak, PinStrengthValidator.evaluate("123"))
    }

    @Test
    fun validate_meetsMinStrength_returnsValid() {
        val result = PinStrengthValidator.validate("482917", PinStrength.Medium)
        assertTrue(result.isValid)
        assertEquals(PinStrength.Strong, result.strength)
        assertNull(result.reason)
    }

    @Test
    fun validate_belowMinStrength_returnsInvalid() {
        val result = PinStrengthValidator.validate("4829", PinStrength.Strong)
        assertFalse(result.isValid)
        assertEquals(PinStrength.Medium, result.strength)
    }

    @Test
    fun validate_weakPattern_returnsReason() {
        val repeated = PinStrengthValidator.validate("111111", PinStrength.Medium)
        assertFalse(repeated.isValid)
        assertEquals(PinWeaknessReason.RepeatedDigits, repeated.reason)

        val ascending = PinStrengthValidator.validate("123456", PinStrength.Medium)
        assertFalse(ascending.isValid)
        assertEquals(PinWeaknessReason.SequentialAscending, ascending.reason)

        val descending = PinStrengthValidator.validate("654321", PinStrength.Medium)
        assertFalse(descending.isValid)
        assertEquals(PinWeaknessReason.SequentialDescending, descending.reason)

        val common = PinStrengthValidator.validate("2580", PinStrength.Weak)
        assertFalse(common.isValid)
        assertEquals(PinWeaknessReason.CommonPin, common.reason)
    }

    @Test
    fun validate_tooShort_returnsTooShortReason() {
        val result = PinStrengthValidator.validate("123", PinStrength.Medium)
        assertFalse(result.isValid)
        assertEquals(PinWeaknessReason.TooShort, result.reason)
    }

    @Test
    fun validate_weakStrengthAcceptsMediumPin() {
        val result = PinStrengthValidator.validate("4829", PinStrength.Weak)
        assertTrue(result.isValid)
        assertEquals(PinStrength.Medium, result.strength)
    }

    @Test
    fun hasRepeatedDigits_detectsAllSame() {
        assertTrue(PinStrengthValidator.hasRepeatedDigits("1111"))
        assertTrue(PinStrengthValidator.hasRepeatedDigits("000000"))
        assertFalse(PinStrengthValidator.hasRepeatedDigits("1212"))
        assertFalse(PinStrengthValidator.hasRepeatedDigits("1"))
    }

    @Test
    fun isAscendingSequence_detectsConsecutiveDigits() {
        assertTrue(PinStrengthValidator.isAscendingSequence("1234"))
        assertTrue(PinStrengthValidator.isAscendingSequence("012345"))
        assertFalse(PinStrengthValidator.isAscendingSequence("1245"))
    }

    @Test
    fun isDescendingSequence_detectsConsecutiveDigits() {
        assertTrue(PinStrengthValidator.isDescendingSequence("4321"))
        assertTrue(PinStrengthValidator.isDescendingSequence("987654"))
        assertFalse(PinStrengthValidator.isDescendingSequence("4210"))
    }
}
