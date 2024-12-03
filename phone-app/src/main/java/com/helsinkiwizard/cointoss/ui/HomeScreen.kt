package com.helsinkiwizard.cointoss.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.helsinkiwizard.cointoss.BuildConfig
import com.helsinkiwizard.cointoss.Constants.BANNER_AD_ID
import com.helsinkiwizard.cointoss.Constants.DEBUG_BANNER_AD_ID
import com.helsinkiwizard.cointoss.ui.viewmodel.HomeScreenContent
import com.helsinkiwizard.cointoss.ui.viewmodel.HomeViewModel
import com.helsinkiwizard.cointoss.utils.AdManager
import com.helsinkiwizard.core.coin.CoinAnimation
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.theme.PercentEighty
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.viewmodel.UiState

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = viewModel.uiState.collectAsState().value) {
            is UiState.ShowContent -> {
                when (val type = state.type as HomeScreenContent) {
                    is HomeScreenContent.LoadingComplete -> {
                        val coinType = viewModel.coinTypeFlow.collectAsState(initial = type.initialCoinType).value
                        val speed = viewModel.speedFlow.collectAsState(initial = type.initialSpeed).value
                        val customCoin = viewModel.customCoinFlow.collectAsState(initial = null).value
                        val adsRemoved = viewModel.adsRemoved.collectAsState(initial = false).value
                        Content(coinType, speed, customCoin, adsRemoved)
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun Content(
    coinType: CoinType,
    speed: Float,
    customCoinUiModel: CustomCoinUiModel?,
    adsRemoved: Boolean,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CoinAnimation(
            coinType = coinType,
            customCoin = customCoinUiModel,
            speed = speed,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(PercentEighty)
                .aspectRatio(1f)
        )
        if (adsRemoved.not()) {
            AdMobBanner(
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun AdMobBanner(
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = if (BuildConfig.DEBUG) DEBUG_BANNER_AD_ID else BANNER_AD_ID
                loadAd(AdManager.getAdRequest())
            }
        }
    )
}
