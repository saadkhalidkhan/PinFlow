package com.pinflow.compose.autofill

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pinflow.compose.PinFlowThemeForTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OtpSuggestionTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun suggestion_appearsWithDetectedCode() {
        composeRule.setContent {
            PinFlowThemeForTest {
                DefaultOtpSuggestion(
                    code = "493721",
                    source = OtpDetectionSource.Clipboard,
                    onApply = {},
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithText("Code detected: 493721").assertIsDisplayed()
        composeRule.onNodeWithText("Tap to paste").assertIsDisplayed()
    }

    @Test
    fun tapSuggestion_invokesApply() {
        var applied = false

        composeRule.setContent {
            PinFlowThemeForTest {
                DefaultOtpSuggestion(
                    code = "493721",
                    source = OtpDetectionSource.Sms,
                    onApply = { applied = true },
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithText("Tap to paste").performClick()
        assertEquals(true, applied)
    }

    @Test
    fun dismiss_hidesSuggestionAction() {
        var dismissed = false

        composeRule.setContent {
            PinFlowThemeForTest {
                DefaultOtpSuggestion(
                    code = "493721",
                    source = OtpDetectionSource.Clipboard,
                    onApply = {},
                    onDismiss = { dismissed = true },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Dismiss OTP suggestion").performClick()
        assertEquals(true, dismissed)
    }
}
