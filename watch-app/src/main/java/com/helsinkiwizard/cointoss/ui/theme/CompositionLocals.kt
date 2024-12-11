package com.helsinkiwizard.cointoss.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

/**
 * Provides access to a [NavHostController] within a composable.
 *
 * @throws IllegalStateException if accessed without a provided `NavHostController`.
 */
val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalNavController is not present. Be sure you have properly set the CompositionLocalProvider")
}
