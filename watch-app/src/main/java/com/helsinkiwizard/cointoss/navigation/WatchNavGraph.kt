package com.helsinkiwizard.cointoss.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import androidx.wear.compose.navigation.composable
import com.helsinkiwizard.cointoss.ui.AboutScreen
import com.helsinkiwizard.cointoss.ui.CoinTossScreen
import com.helsinkiwizard.cointoss.ui.SettingsScreen
import com.helsinkiwizard.cointoss.ui.coinlist.CoinListScreen

const val MAIN_ROUTE = "mainNavRoute"

enum class NavRoute {
    Home,
    CoinList,
    Settings,
    About
}

fun NavGraphBuilder.mainGraph() {
    navigation(
        startDestination = NavRoute.Home.name,
        route = MAIN_ROUTE
    ) {
        composable(NavRoute.Home.name) {
            CoinTossScreen()
        }
        composable(NavRoute.CoinList.name) {
            CoinListScreen()
        }
        composable(NavRoute.Settings.name) {
            SettingsScreen()
        }
        composable(NavRoute.About.name) {
            AboutScreen()
        }
    }
}
