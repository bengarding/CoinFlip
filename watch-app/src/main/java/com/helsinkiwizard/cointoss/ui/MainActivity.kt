package com.helsinkiwizard.cointoss.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.MaterialTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.analytics.FirebaseAnalytics
import com.helsinkiwizard.cointoss.Constants.APP_DRAWER
import com.helsinkiwizard.cointoss.Constants.EXTRA_START_FLIPPING
import com.helsinkiwizard.cointoss.Constants.TILE
import com.helsinkiwizard.cointoss.ui.coinlist.Coin
import com.helsinkiwizard.cointoss.ui.coinlist.CoinList
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinTossViewModel
import com.helsinkiwizard.core.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.LocalActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val viewModel: CoinTossViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel.startFlipping = intent.extras?.getBoolean(EXTRA_START_FLIPPING) ?: false

        setContent {
            CoinTossTheme {
                CompositionLocalProvider(
                    LocalActivity provides this
                ) {
                    CoinToss()
                }
            }
        }

        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ORIGIN, if (viewModel.startFlipping) TILE else APP_DRAWER)
        }
        FirebaseAnalytics.getInstance(applicationContext).logEvent(FirebaseAnalytics.Event.APP_OPEN, params)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.startFlipping = intent?.extras?.getBoolean(EXTRA_START_FLIPPING) ?: false
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CoinToss(
    viewModel: CoinTossViewModel = hiltViewModel(LocalActivity.current)
) {
    val coinType = viewModel.coinTypeFlow.collectAsState().value
    val customCoin = viewModel.customCoinFlow.collectAsState(initial = null).value

    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(count = 2, state = pagerState) { page ->
            when (page) {
                0 -> Coin(
                    coinType = coinType,
                    customCoin = customCoin,
                    pagerState = pagerState,
                    startFlipping = viewModel.startFlipping,
                    onStartFlipping = {
                        viewModel.startFlipping = false
                    }
                )

                1 -> CoinList()
            }
        }
    }
}
