package com.helsinkiwizard.cointoss.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.helsinkiwizard.cointoss.ui.AboutScreen
import com.helsinkiwizard.cointoss.ui.AttributionsScreen
import com.helsinkiwizard.cointoss.ui.CoinListScreen
import com.helsinkiwizard.cointoss.ui.CreateCoinScreen
import com.helsinkiwizard.cointoss.ui.HomeScreen
import com.helsinkiwizard.cointoss.ui.SettingsScreen

const val MAIN_ROUTE = "mainNavRoute"

enum class NavRoute {
    Home,
    CoinList,
    Settings,
    About,
    Attributions,
    CreateCoin
}

fun NavGraphBuilder.mainGraph(navController: NavController) {
    navigation(
        startDestination = NavRoute.Home.name,
        route = MAIN_ROUTE
    ) {
        composable(NavRoute.Home.name) {
            HomeScreen()
        }
        composable(NavRoute.CoinList.name) {
            CoinListScreen(navController)
        }
        composable(NavRoute.Settings.name) {
            SettingsScreen()
        }
        composable(NavRoute.About.name) {
            AboutScreen(navController)
        }
        composable(NavRoute.Attributions.name) {
            AttributionsScreen()
        }
        composable(NavRoute.CreateCoin.name) {
            CreateCoinScreen(navController)
        }
    }
}
