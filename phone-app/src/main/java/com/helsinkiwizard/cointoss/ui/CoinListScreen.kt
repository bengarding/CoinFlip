package com.helsinkiwizard.cointoss.ui

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.navigation.NavRoute
import com.helsinkiwizard.cointoss.ui.theme.Mulish
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinListContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinListViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.UiState
import com.helsinkiwizard.core.CoreConstants
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.theme.BlackTransparent
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.LargeCoinButtonHeight
import com.helsinkiwizard.core.theme.Sixty
import com.helsinkiwizard.core.theme.Text16
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.utils.AutoSizeText

@Composable
internal fun CoinListScreen(
    navController: NavController,
    viewModel: CoinListViewModel = hiltViewModel()
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is UiState.ShowContent -> {
            when (state.type as CoinListContent) {
                is CoinListContent.LoadingComplete -> Content(viewModel)
                is CoinListContent.CoinSet -> navController.navigate(NavRoute.Home.name)
            }
        }

        else -> {}
    }
}

@Composable
private fun Content(viewModel: CoinListViewModel) {
    val context = LocalContext.current
    val coinList = remember { CoinType.entries.sortedBy { context.getString(it.nameRes) } }

    LazyColumn(
        contentPadding = PaddingValues(vertical = Forty),
        verticalArrangement = Arrangement.spacedBy(Eight),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Sixty)
    ) {
        items(coinList) { coin ->
            Coin(
                coin = coin,
                onClick = { viewModel.onCoinClick(coin) }
            )
        }
    }
}

@Composable
private fun Coin(
    coin: CoinType,
    onClick: () -> Unit
) {
    val analytics = FirebaseAnalytics.getInstance(LocalContext.current)
    Box(
        modifier = Modifier
            .height(LargeCoinButtonHeight)
            .clickable {
                onClick()

                val name = coin.name
                    .lowercase()
                    .replaceFirstChar { it.titlecase() }
                val params = Bundle().apply {
                    putString(CoreConstants.COIN_SELECTED, name)
                }
                analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
            }
    ) {
        Image(
            painter = painterResource(id = coin.heads),
            contentDescription = stringResource(id = coin.nameRes),
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop
        )
        AutoSizeText(
            text = stringResource(id = coin.nameRes),
            fontFamily = Mulish,
            fontWeight = FontWeight.Normal,
            maxFontSize = Text16,
            color = Color.White,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(color = BlackTransparent, shape = CircleShape)
                .padding(vertical = Four, horizontal = Twelve)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinListPreview() {
    val viewModel = CoinListViewModel(Repository(LocalContext.current))
    CoinListScreen(NavController(LocalContext.current), viewModel)
}
