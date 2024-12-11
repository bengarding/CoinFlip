package com.helsinkiwizard.cointoss.ui.menu

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.helsinkiwizard.cointoss.navigation.NavRoute

internal class MenuItem(
    val route: NavRoute,
    @StringRes val title: Int,
    val icon: ImageVector
)
