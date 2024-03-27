package com.helsinkiwizard.cointoss.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.data.ThemeMode

@Composable
fun CoinTossTheme(
    repository: Repository? = null, // nullable for previews
    themeMode: ThemeMode = ThemeMode.LIGHT, // default for previews
    initialMaterialYou: Boolean? = null, // nullable for previews
    content: @Composable () -> Unit
) {
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val dynamicColor = repository?.getMaterialYou?.collectAsState(initial = initialMaterialYou)?.value

    val darkTheme by remember(themeMode) {
        mutableStateOf(
            when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme
                else -> false
            }
        )
    }

    val colorScheme = when {
        dynamicColor == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context).materialYouDark()
            } else {
                dynamicLightColorScheme(context).materialYouLight()
            }
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = onPrimaryLight,
    surfaceContainerHighest = primaryLight, // link color
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = primaryDark,
    surfaceContainerHighest = onPrimaryDark, // link color
)

private fun ColorScheme.materialYouLight(): ColorScheme {
    return lightColorScheme(
        primary = this.primary,
        onPrimary = this.onPrimary,
        primaryContainer = this.primaryContainer,
        onPrimaryContainer = this.onPrimaryContainer,
        secondary = this.secondary,
        onSecondary = this.onSecondary,
        secondaryContainer = this.secondaryContainer,
        onSecondaryContainer = this.onSecondaryContainer,
        tertiary = this.tertiary,
        onTertiary = this.onTertiary,
        tertiaryContainer = this.tertiaryContainer,
        onTertiaryContainer = this.onTertiaryContainer,
        error = this.error,
        onError = this.onError,
        errorContainer = this.errorContainer,
        onErrorContainer = this.onErrorContainer,
        background = this.background,
        onBackground = this.onBackground,
        surface = this.surface,
        onSurface = this.onSurface,
        surfaceVariant = this.surfaceVariant,
        onSurfaceVariant = this.onSurfaceVariant,
        outline = this.outline,
        outlineVariant = this.outlineVariant,
        scrim = this.scrim,
        inverseSurface = this.inverseSurface,
        inverseOnSurface = this.inverseOnSurface,
        inversePrimary = this.inversePrimary,
        surfaceDim = this.surfaceDim,
        surfaceBright = this.surfaceBright,
        surfaceContainerLowest = this.surfaceContainerLowest,
        surfaceContainerLow = this.surfaceContainerLow,
        surfaceContainer = this.surfaceContainer,
        surfaceContainerHigh = this.onPrimary,
        surfaceContainerHighest = this.primary, // link color
    )
}

private fun ColorScheme.materialYouDark(): ColorScheme {
    return darkColorScheme(
        primary = this.onPrimary,
        onPrimary = this.primary,
        primaryContainer = this.primaryContainer,
        onPrimaryContainer = this.onPrimaryContainer,
        secondary = this.secondary,
        onSecondary = this.onSecondary,
        secondaryContainer = this.secondaryContainer,
        onSecondaryContainer = this.onSecondaryContainer,
        tertiary = this.tertiary,
        onTertiary = this.onTertiary,
        tertiaryContainer = this.tertiaryContainer,
        onTertiaryContainer = this.onTertiaryContainer,
        error = this.error,
        onError = this.onError,
        errorContainer = this.errorContainer,
        onErrorContainer = this.onErrorContainer,
        background = this.background,
        onBackground = this.onBackground,
        surface = this.surface,
        onSurface = this.onSurface,
        surfaceVariant = this.surfaceVariant,
        onSurfaceVariant = this.onSurfaceVariant,
        outline = this.outline,
        outlineVariant = this.outlineVariant,
        scrim = this.scrim,
        inverseSurface = this.inverseSurface,
        inverseOnSurface = this.inverseOnSurface,
        inversePrimary = this.inversePrimary,
        surfaceDim = this.surfaceDim,
        surfaceBright = this.surfaceBright,
        surfaceContainerLowest = this.surfaceContainerLowest,
        surfaceContainerLow = this.surfaceContainerLow,
        surfaceContainer = this.surfaceContainer,
        surfaceContainerHigh = this.onPrimary,
        surfaceContainerHighest = this.primary, // link color
    )
}
