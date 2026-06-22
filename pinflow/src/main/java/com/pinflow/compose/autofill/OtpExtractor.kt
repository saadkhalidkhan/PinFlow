package com.pinflow.compose.autofill

/**
 * Extracts OTP-like codes from free-form text such as SMS bodies or clipboard content.
 */
object OtpExtractor {

    private val otpContextKeywords = listOf(
        "code",
        "otp",
        "pin",
        "passcode",
        "verification",
        "verify",
        "token",
    )

    /**
     * Returns an OTP of [length] when a confident match is found, otherwise `null`.
     *
     * Prefers isolated sequences of exactly [length] characters so phone numbers and long
     * transaction IDs are not split incorrectly.
     */
    fun extract(
        text: String,
        length: Int,
        allowedChars: OtpAllowedChars = OtpAllowedChars.DigitsOnly,
    ): String? {
        if (length <= 0 || text.isBlank()) return null

        val element = when (allowedChars) {
            OtpAllowedChars.DigitsOnly -> "\\d"
            OtpAllowedChars.AlphaNumeric -> "[A-Za-z0-9]"
        }

        val isolatedExact = Regex(
            "(?<![$element])($element{$length})(?![$element])",
            setOf(RegexOption.IGNORE_CASE),
        )

        val matches = isolatedExact.findAll(text).map { it.groupValues[1] }.toList()
        if (matches.isEmpty()) return null

        val selected = when {
            matches.size == 1 -> matches.first()
            else -> pickBestMatch(text, matches)
        }

        return normalize(selected, allowedChars)
    }

    private fun pickBestMatch(text: String, matches: List<String>): String {
        val lower = text.lowercase()
        return matches.maxByOrNull { code ->
            val index = text.indexOf(code, ignoreCase = true)
            val contextStart = (index - 15).coerceAtLeast(0)
            val contextEnd = (index + code.length + 10).coerceAtMost(lower.length)
            val context = lower.substring(contextStart, contextEnd)
            otpContextKeywords.count { keyword -> keyword in context }
        } ?: matches.first()
    }

    private fun normalize(code: String, allowedChars: OtpAllowedChars): String =
        when (allowedChars) {
            OtpAllowedChars.DigitsOnly -> code
            OtpAllowedChars.AlphaNumeric -> code.uppercase()
        }
}
