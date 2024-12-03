package com.helsinkiwizard.cointoss.ui.composable

import androidx.activity.ComponentActivity
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.helsinkiwizard.cointoss.data.ThemeMode
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.theme.LocalNavController
import com.helsinkiwizard.core.theme.LocalActivity

@Composable
fun PreviewSurface(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    CoinTossTheme(themeMode = themeMode) {
        Surface {
            CompositionLocalProvider(
                LocalActivity provides ComponentActivity(),
                LocalNavController provides NavHostController(LocalContext.current)
            ) {
                content()
            }
        }
    }
}
