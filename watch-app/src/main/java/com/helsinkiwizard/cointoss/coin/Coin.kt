package com.helsinkiwizard.cointoss.coin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.helsinkiwizard.core.coin.CoinAnimation
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel

@OptIn(ExperimentalPagerApi::class) // pager
@Composable
fun Coin(
    coinType: CoinType,
    customCoin: CustomCoinUiModel?,
    pagerState: PagerState,
    startFlipping: Boolean,
    onStartFlipping: () -> Unit
) {
    var showChevron by remember { mutableStateOf(startFlipping.not()) }
    Chevron(showChevron)

    Box(
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(coinType, startFlipping) {
            // When a new coin type is selected, move page to this Composable
            if (pagerState.currentPage != 0) {
                pagerState.animateScrollToPage(0)
            }
        }

        CoinAnimation(
            coinType = coinType,
            customCoin = customCoin,
            startFlipping = startFlipping,
            onStartFlipping = onStartFlipping,
            onFlip = { showChevron = false },
            modifier = Modifier.fillMaxSize(),
            speed = 3f
        )
    }
}
