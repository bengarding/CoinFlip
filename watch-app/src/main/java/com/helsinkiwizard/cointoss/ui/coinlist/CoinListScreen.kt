package com.helsinkiwizard.cointoss.ui.coinlist

import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.tiles.TileService
import androidx.wear.tooling.preview.devices.WearDevices
import coil.compose.SubcomposeAsyncImage
import com.google.android.gms.wearable.Wearable
import com.google.firebase.analytics.FirebaseAnalytics
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.tile.CoinTileService
import com.helsinkiwizard.cointoss.ui.DownloadMobileAppConfirmation
import com.helsinkiwizard.cointoss.ui.ProgressIndicator
import com.helsinkiwizard.cointoss.ui.ShowOnPhoneConfirmation
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinListDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CoinListViewModel
import com.helsinkiwizard.core.CoreConstants.COIN_SELECTED
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.coin.CoinType.BITCOIN
import com.helsinkiwizard.core.coin.CoinType.CUSTOM
import com.helsinkiwizard.core.theme.BlackTransparent
import com.helsinkiwizard.core.theme.CoinButtonHeight
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.OnPrimaryContainerDark
import com.helsinkiwizard.core.theme.PercentEighty
import com.helsinkiwizard.core.theme.PrimaryContainerDark
import com.helsinkiwizard.core.theme.Text14
import com.helsinkiwizard.core.theme.Text20
import com.helsinkiwizard.core.theme.ThirtyTwo
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.utils.buildTextWithLink
import com.helsinkiwizard.core.utils.getEmailIntent
import com.helsinkiwizard.core.utils.onLinkClick
import com.helsinkiwizard.core.viewmodel.DialogState
import com.helsinkiwizard.core.viewmodel.UiState
import kotlinx.coroutines.launch

@Composable
fun CoinListScreen(viewModel: CoinListViewModel = hiltViewModel()) {
    CoinListContent(viewModel)
    Dialogs(viewModel)
}

@Composable
private fun Dialogs(viewModel: CoinListViewModel) {
    when (val state = viewModel.dialogState.collectAsState().value) {
        is DialogState.ShowContent -> {
            when (val type = state.type as CoinListDialogs) {
                is CoinListDialogs.OpenOnPhone -> {
                    ShowOnPhoneConfirmation(
                        messageRes = type.messageRes,
                        onTimeout = { viewModel.resetDialogState() }
                    )
                }

                is CoinListDialogs.DownloadMobileApp -> {
                    DownloadMobileAppConfirmation(
                        onClick = { viewModel.resetDialogState() }
                    )
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun CoinListContent(viewModel: CoinListViewModel) {
    when (viewModel.uiState.collectAsState().value) {
        is UiState.ShowContent -> CoinList(viewModel)
        is UiState.Loading -> ProgressIndicator()
        else -> {}
    }
}

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
private fun CoinList(viewModel: CoinListViewModel) {
    val customCoin = viewModel.customCoinFlow.collectAsState(initial = null).value
    val listState = rememberScalingLazyListState()
    Scaffold(
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        val context = LocalContext.current
        val focusRequester = rememberActiveFocusRequester()
        val coroutineScope = rememberCoroutineScope()
        val sortedCoins = remember {
            CoinType.entries
                .filterNot { it == CUSTOM }
                .sortedBy { context.getString(it.nameRes) }
        }

        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .onRotaryScrollEvent {
                    // https://developer.android.com/training/wearables/compose/rotary-input
                    coroutineScope.launch {
                        listState.scrollBy(it.verticalScrollPixels)
                        listState.animateScrollBy(0f)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
        ) {
            item { ListTitle() }
            item {
                if (customCoin != null) {
                    CoinButton(
                        coin = CUSTOM,
                        name = customCoin.name,
                        customCoinHeadsUri = customCoin.headsUri
                    )
                } else {
                    val capabilityClient by lazy { Wearable.getCapabilityClient(context) }
                    val nodeClient by lazy { Wearable.getNodeClient(context) }
                    val remoteActivityHelper by lazy { RemoteActivityHelper(context) }
                    BlankCustomCoin(
                        onclick = {
                            viewModel.onBlankCustomCoinClicked(capabilityClient, nodeClient, remoteActivityHelper)
                        }
                    )
                }
            }
            items(sortedCoins) { coin ->
                CoinButton(coin)
            }
            item { RequestCoin() }
        }
    }
}

@Composable
fun ListTitle() {
    Text(
        text = stringResource(id = R.string.choose_a_coin),
        fontSize = Text20,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(bottom = Eight)
            .fillMaxWidth(PercentEighty)
    )
}

@Composable
fun CoinButton(
    coin: CoinType,
    name: String = EMPTY_STRING,
    customCoinHeadsUri: Uri? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = Repository(context)
    val analytics = FirebaseAnalytics.getInstance(context)

    Button(
        onClick = {
            scope.launch {
                val coinTypeName = coin.name.lowercase().replaceFirstChar { it.titlecase() }
                val params = Bundle().apply {
                    putString(COIN_SELECTED, coinTypeName)
                }
                analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, params)
                dataStore.setCoinType(coin)
                TileService.getUpdater(context).requestUpdate(CoinTileService::class.java)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(CoinButtonHeight)
    ) {
        Box {
            if (coin == CUSTOM) {
                SubcomposeAsyncImage(
                    model = customCoinHeadsUri,
                    contentDescription = name.ifEmpty { stringResource(id = R.string.custom_coin) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Black),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                )
            } else {
                Image(
                    painter = painterResource(id = coin.heads),
                    contentDescription = stringResource(id = coin.nameRes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Black),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop
                )
            }
            CoinLabel(
                name = name,
                nameRes = coin.nameRes
            )
        }
    }
}

@Composable
private fun BlankCustomCoin(
    onclick: () -> Unit
) {
    Button(
        onClick = onclick,
        modifier = Modifier
            .fillMaxWidth()
            .height(CoinButtonHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = PrimaryContainerDark)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = OnPrimaryContainerDark,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(Forty)
                    .fillMaxSize()
            )
            CoinLabel(
                name = EMPTY_STRING,
                nameRes = R.string.create_a_coin
            )
        }
    }
}

@Composable
private fun BoxScope.CoinLabel(
    name: String,
    nameRes: Int
) {
    Text(
        text = name.ifEmpty { stringResource(id = nameRes) },
        fontSize = Text14,
        fontWeight = FontWeight.Normal,
        color = Color.White,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .background(color = BlackTransparent, shape = CircleShape)
            .padding(vertical = Four, horizontal = Twelve)
    )
}

@Composable
private fun RequestCoin() {
    val context = LocalContext.current
    val emailAddress = stringResource(id = R.string.email_address)
    val annotatedString = buildTextWithLink(
        fullText = stringResource(id = R.string.request_coin),
        linkText = emailAddress
    )
    ClickableText(
        text = annotatedString,
        modifier = Modifier.padding(start = Twelve, top = ThirtyTwo, end = Twelve, bottom = Forty),
        onClick = { offset ->
            annotatedString.onLinkClick(
                offset = offset,
                onClick = {
                    context.startActivity(getEmailIntent(emailAddress))
                }
            )
        }
    )
}

@Preview(name = "large round", device = WearDevices.LARGE_ROUND)
@Preview(name = "square", device = WearDevices.SQUARE)
@Composable
private fun CoinButtonPreview() {
    CoinButton(coin = BITCOIN)
}

@Preview(name = "large round", device = WearDevices.LARGE_ROUND)
@Preview(name = "square", device = WearDevices.SQUARE)
@Composable
private fun CoinListPreview() {
    CoinListScreen()
}
