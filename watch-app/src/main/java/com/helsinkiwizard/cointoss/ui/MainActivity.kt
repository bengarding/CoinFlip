package com.helsinkiwizard.cointoss.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.analytics.FirebaseAnalytics
import com.helsinkiwizard.cointoss.Constants.APP_DRAWER
import com.helsinkiwizard.cointoss.Constants.EXTRA_START_FLIPPING
import com.helsinkiwizard.cointoss.Constants.TILE
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.coin.Coin
import com.helsinkiwizard.cointoss.coin.CoinList
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.theme.CoinTossTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var repo: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val startFlipping = intent.extras?.getBoolean(EXTRA_START_FLIPPING) ?: false
        val initialCoinType = runBlocking { repo.getCoinType.firstOrNull() ?: CoinType.BITCOIN }

        setContent {
            CoinTossTheme {
                CoinFlip(initialCoinType, startFlipping)
            }
        }

        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ORIGIN, if (startFlipping) TILE else APP_DRAWER)
        }
        FirebaseAnalytics.getInstance(applicationContext).logEvent(FirebaseAnalytics.Event.APP_OPEN, params)
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun CoinFlip(initialCoinType: CoinType, startFlippingIntent: Boolean) {
        val coinType = repo.getCoinType.collectAsState(initial = initialCoinType).value
        val customCoin = repo.getCustomCoin.collectAsState(initial = null).value

        val pagerState = rememberPagerState()
        var startFlipping by remember { mutableStateOf(startFlippingIntent) }

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
                        startFlipping = startFlipping,
                        onStartFlipping = {
                            startFlipping = false
                        }
                    )

                    1 -> CoinList()
                }
            }
        }
    }
}
