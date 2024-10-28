package com.helsinkiwizard.cointoss.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.HomeScreenContent
import com.helsinkiwizard.cointoss.ui.viewmodel.HomeViewModel
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
                        Content(coinType, speed, customCoin)
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
    customCoinUiModel: CustomCoinUiModel?
) {
    CoinAnimation(
        coinType = coinType,
        customCoin = customCoinUiModel,
        speed = speed,
        modifier = Modifier
            .fillMaxWidth(PercentEighty)
            .aspectRatio(1f)
    )
}
