package com.helsinkiwizard.cointoss.ui

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.google.firebase.analytics.FirebaseAnalytics
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.navigation.NavRoute
import com.helsinkiwizard.cointoss.ui.composable.PreviewSurface
import com.helsinkiwizard.cointoss.ui.theme.LocalNavController
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinListContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinListDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinListViewModel
import com.helsinkiwizard.cointoss.utils.launchInAppReview
import com.helsinkiwizard.core.CoreConstants
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.theme.Alpha20
import com.helsinkiwizard.core.theme.BlackTransparent
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.LargeCoinButtonHeight
import com.helsinkiwizard.core.theme.LocalActivity
import com.helsinkiwizard.core.theme.Sixty
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.Two
import com.helsinkiwizard.core.ui.composable.CoinListShape
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.utils.ifNullOrEmpty
import com.helsinkiwizard.core.viewmodel.DialogState
import com.helsinkiwizard.core.viewmodel.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun CoinListScreen(
    viewModel: CoinListViewModel = hiltViewModel()
) {
    CoinListContent(viewModel)
    CoinListDialogs(viewModel)
}

@Composable
private fun CoinListContent(
    viewModel: CoinListViewModel
) {
    val navController = LocalNavController.current
    when (val state = viewModel.uiState.collectAsState().value) {
        is UiState.ShowContent -> {
            when (val type = state.type as CoinListContent) {
                is CoinListContent.LoadingComplete -> CoinList(viewModel, type.customCoinFlow, navController)
                is CoinListContent.CoinSet -> navController.navigate(NavRoute.Home.name)
            }
        }

        else -> {}
    }
}

@Composable
private fun CoinListDialogs(
    viewModel: CoinListViewModel
) {
    val activity = LocalActivity.current
    when (val state = viewModel.dialogState.collectAsState().value) {
        is DialogState.ShowContent -> {
            when (val type = state.type as CoinListDialogs) {
                is CoinListDialogs.InAppReview -> {
                    activity.launchInAppReview(
                        onComplete = type.onComplete
                    )
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun CoinList(
    viewModel: CoinListViewModel,
    customCoinFlow: Flow<CustomCoinUiModel?>,
    navController: NavController
) {
    val context = LocalContext.current
    val coinList = remember {
        CoinType.entries
            .filterNot { it == CoinType.CUSTOM }
            .sortedBy { context.getString(it.nameRes) }
    }
    val customCoin = customCoinFlow.collectAsState(initial = null).value

    LazyColumn(
        contentPadding = PaddingValues(vertical = Forty),
        verticalArrangement = Arrangement.spacedBy(Eight),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Sixty)
    ) {
        item {
            CustomCoin(
                coin = customCoin,
                onClick = {
                    if (customCoin == null) {
                        navController.navigate(NavRoute.CreateCoin.name)
                    } else {
                        viewModel.onCoinClick(CoinType.CUSTOM)
                    }
                }
            )
        }
        items(coinList) { coin ->
            Coin(
                coin = coin,
                onClick = { viewModel.onCoinClick(coin) }
            )
        }
    }
}

@Composable
private fun CustomCoin(
    coin: CustomCoinUiModel?,
    onClick: () -> Unit
) {
    val analytics = FirebaseAnalytics.getInstance(LocalContext.current)
    val coinName = coin?.name
    Box(
        modifier = Modifier
            .height(LargeCoinButtonHeight)
            .clip(CoinListShape())
            .clip(RectangleShape)
            .clickable {
                onClick()
                val name = if (coin == null) "Create a coin" else "Custom coin"
                val params = Bundle().apply {
                    putString(CoreConstants.COIN_SELECTED, name)
                }
                analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
            }
    ) {
        var showDefault by remember { mutableStateOf(false) }

        AsyncImage(
            model = coin?.headsUri,
            contentDescription = coinName.ifNullOrEmpty { stringResource(id = R.string.create_a_coin) },
            alignment = Alignment.Center,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .background(if (showDefault) MaterialTheme.colorScheme.surfaceContainerHighest.copy(Alpha20) else MaterialTheme.colorScheme.primary),
            onState = { state ->
                showDefault = state is AsyncImagePainter.State.Empty || state is AsyncImagePainter.State.Error
            }
        )
        if (showDefault) {
            Box(
                modifier = Modifier
                    .height(LargeCoinButtonHeight)
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = MaterialTheme.colorScheme.surfaceContainerHighest),
                        onClick = onClick
                    )
            ) {
                Box(
                    modifier = Modifier
                        .height(LargeCoinButtonHeight)
                        .fillMaxWidth()
                        .border(
                            width = Two,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RectangleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .height(LargeCoinButtonHeight)
                        .fillMaxWidth()
                        .border(
                            width = Two,
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = CoinListShape()
                        )
                )
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(Forty)
                        .fillMaxSize()
                )
            }
        }
        val label = when {
            coinName.isNullOrEmpty().not() -> coinName!!
            coin != null -> stringResource(id = R.string.custom_coin)
            else -> stringResource(id = R.string.create_a_coin)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(color = BlackTransparent, shape = CircleShape)
                .padding(vertical = Four, horizontal = Twelve)
        )
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
        Text(
            text = stringResource(id = coin.nameRes),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
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
    val navController = NavController(LocalContext.current)
    PreviewSurface {
        CoinList(viewModel, flowOf(), navController)
    }
}
