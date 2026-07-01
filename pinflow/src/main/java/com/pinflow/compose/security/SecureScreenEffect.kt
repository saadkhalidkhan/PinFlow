package com.pinflow.compose.security

import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

/**
 * Applies [WindowManager.LayoutParams.FLAG_SECURE] while [enabled] is true, preventing
 * screenshots and screen recording for the host window. Restores the previous flag on dispose.
 *
 * Host apps can also set FLAG_SECURE globally; this effect is optional per-screen.
 */
@Composable
fun SecureScreenEffect(enabled: Boolean) {
    if (!enabled) return

    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.context as? android.app.Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
