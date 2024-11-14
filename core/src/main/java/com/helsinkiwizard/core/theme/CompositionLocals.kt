package com.helsinkiwizard.core.theme

import androidx.activity.ComponentActivity
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Provides access to a [ComponentActivity] within a composable.
 *
 * @throws IllegalStateException if accessed without a provided `ComponentActivity`.
 */
val LocalActivity = staticCompositionLocalOf<ComponentActivity> {
    error("LocalActivity is not present. Be sure you have properly set the CompositionLocalProvider")
}
