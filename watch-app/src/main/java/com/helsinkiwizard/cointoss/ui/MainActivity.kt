package com.helsinkiwizard.cointoss.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.horologist.compose.layout.fillMaxRectangle
import com.google.firebase.analytics.FirebaseAnalytics
import com.helsinkiwizard.cointoss.Constants.APP_DRAWER
import com.helsinkiwizard.cointoss.Constants.EXTRA_START_FLIPPING
import com.helsinkiwizard.cointoss.Constants.TILE
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.coin.Coin
import com.helsinkiwizard.cointoss.coin.CoinList
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Text20
import com.helsinkiwizard.core.theme.Twelve
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    @Inject
    lateinit var repo: Repository

    private var startFlipping by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        startFlipping = intent.extras?.getBoolean(EXTRA_START_FLIPPING) ?: false
        val initialCoinType = runBlocking { repo.getCoinType.firstOrNull() ?: CoinType.BITCOIN }

        setContent {
            CoinTossTheme {
                CoinToss(initialCoinType)
                CustomCoinDialog()
            }
        }

        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ORIGIN, if (startFlipping) TILE else APP_DRAWER)
        }
        FirebaseAnalytics.getInstance(applicationContext).logEvent(FirebaseAnalytics.Event.APP_OPEN, params)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        startFlipping = intent?.extras?.getBoolean(EXTRA_START_FLIPPING) ?: false
    }

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun CoinToss(initialCoinType: CoinType) {
        val coinType = repo.getCoinType.collectAsState(initial = initialCoinType).value
        val customCoin = repo.getCustomCoin.collectAsState(initial = null).value

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

    @Composable
    private fun CustomCoinDialog() {
        val showCustomCoinDialog = repo.getShowSendToWatchDialog.collectAsState(initial = false).value
        Dialog(
            showDialog = showCustomCoinDialog,
            onDismissRequest = {
                // do nothing
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Twelve),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxRectangle()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(id = R.string.new_feature_alert),
                    fontSize = Text20,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.custom_coin_dialog_message),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {
                        lifecycleScope.launch {
                            repo.disableShowSendToWatchDialog()
                        }
                    },
                    modifier = Modifier.padding(top = Eight)
                ) {
                    Text(
                        text = stringResource(id = R.string.custom_coin_dialog_got_it)
                    )
                }
            }
        }
    }
}
