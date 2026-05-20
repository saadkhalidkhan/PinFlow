package com.pinflow.compose

object PinFlowValidator {

    fun isComplete(value: String, length: Int): Boolean =
        value.length == length

    fun isNumeric(value: String): Boolean =
        value.isNotEmpty() && value.all { it.isDigit() }

    fun hasRepeatedDigits(value: String): Boolean {
        if (value.length < 2) return false
        return value.all { it == value.first() }
    }
}
