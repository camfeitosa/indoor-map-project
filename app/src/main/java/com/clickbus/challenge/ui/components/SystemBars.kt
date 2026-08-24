package com.clickbus.challenge.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Sets the status bar icon color for the screen it's called from (edge-to-edge content draws
 * behind the status bar, so each screen picks icon contrast based on what's under it).
 */
@Composable
fun StatusBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val activity = LocalContext.current as? Activity ?: return
    SideEffect {
        WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars = darkIcons
    }
}
