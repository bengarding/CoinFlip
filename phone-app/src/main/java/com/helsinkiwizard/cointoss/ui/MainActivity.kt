package com.helsinkiwizard.cointoss.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.helsinkiwizard.cointoss.Constants.EXTRA_MATERIAL_YOU
import com.helsinkiwizard.cointoss.Constants.EXTRA_THEME_MODE
import com.helsinkiwizard.cointoss.Constants.NAV_TRANSITION_DURATION
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.data.ThemeMode
import com.helsinkiwizard.cointoss.navigation.MAIN_ROUTE
import com.helsinkiwizard.cointoss.navigation.NavRoute
import com.helsinkiwizard.cointoss.navigation.mainGraph
import com.helsinkiwizard.cointoss.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.drawer.DrawerContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val themeModeName = intent.extras?.getString(EXTRA_THEME_MODE) ?: ThemeMode.SYSTEM.name
        val initialMaterialYou = intent.extras?.getBoolean(EXTRA_MATERIAL_YOU) ?: true
        val initialThemeMode = ThemeMode.valueOf(themeModeName)

        setContent {
            val themeMode = repository.getThemeMode.collectAsState(initial = initialThemeMode).value

            CoinTossTheme(repository, themeMode, initialMaterialYou) {
                CoinToss(themeMode)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CoinToss(themeMode: ThemeMode) {
        val navController: NavHostController = rememberNavController()
        val drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        val currentDestination = navController.currentBackStackEntryAsState().value?.destination
        val currentRoute = NavRoute.valueOf(currentDestination?.route ?: NavRoute.Home.name)

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Title(currentRoute) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Menu,
                                contentDescription = stringResource(id = R.string.menu)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                color = MaterialTheme.colorScheme.surface
            ) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        DrawerContent(
                            onClick = { selectedRoute ->
                                coroutineScope.launch {
                                    if (currentRoute != selectedRoute) {
                                        navController.navigate(selectedRoute.name)
                                    }
                                    drawerState.close()
                                }
                            }
                        )
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = MAIN_ROUTE,
                        enterTransition = { fadeIn(tween(NAV_TRANSITION_DURATION)) },
                        exitTransition = { fadeOut(tween(NAV_TRANSITION_DURATION)) }
                    ) {
                        mainGraph(navController)
                    }
                }
            }
        }
    }

    @Composable
    private fun Title(currentRoute: NavRoute) {
        val title = stringResource(
            id = when (currentRoute) {
                NavRoute.Home -> R.string.app_name
                NavRoute.CoinList -> R.string.choose_a_coin
                NavRoute.Settings -> R.string.settings
                NavRoute.About -> R.string.about
                NavRoute.Attributions -> R.string.attributions
                NavRoute.CreateCoin -> R.string.create_a_coin
            }
        )
        AnimatedContent(
            targetState = title,
            label = "title"
        ) {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun topBarColors(themeMode: ThemeMode): TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        scrolledContainerColor = MaterialTheme.colorScheme.primary,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
        titleContentColor = MaterialTheme.colorScheme.onPrimary,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
    )
}
