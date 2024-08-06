package com.helsinkiwizard.cointoss.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.composable.dialog.CoinTossDialog
import com.helsinkiwizard.cointoss.ui.viewmodel.DialogState
import com.helsinkiwizard.cointoss.ui.viewmodel.HomeScreenContent
import com.helsinkiwizard.cointoss.ui.viewmodel.HomeScreenDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.HomeViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.UiState
import com.helsinkiwizard.core.coin.CoinAnimation
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.PercentEighty
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel

private const val SEND_TO_WATCH_ICON_ID = "send_to_watch"

private val inlineIconMap = mapOf(
    SEND_TO_WATCH_ICON_ID to InlineTextContent(
        Placeholder(30.sp, 30.sp, PlaceholderVerticalAlign.TextCenter)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_send_to_watch),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Four)
        )
    }
)

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
        Dialogs(
            state = viewModel.dialogState.collectAsState().value,
            onDismiss = { viewModel.resetDialogState() }
        )
    }
}

@Composable
private fun Dialogs(
    state: DialogState,
    onDismiss: () -> Unit
) {
    when (state) {
        is DialogState.ShowContent -> {
            when (state.type as HomeScreenDialogs) {
                is HomeScreenDialogs.ShowSendToWatchDialog -> {
                    val bodyMedium = MaterialTheme.typography.bodyMedium.toSpanStyle()
                    val bodyMediumSemiBold = MaterialTheme.typography.bodyMedium.toSpanStyle()

                    val stepOne = buildAnnotatedString {
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_1))
                        }
                        addStyle(bodyMediumSemiBold, 0, 1)
                    }

                    val stepTwo = buildAnnotatedString {
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_2))
                        }
                        addStyle(bodyMediumSemiBold, 0, 1)
                    }

                    val stepThreeFull = buildAnnotatedString {
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_3_start))
                        }
                        appendInlineContent(SEND_TO_WATCH_ICON_ID)
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_3_end))
                        }
                        addStyle(bodyMediumSemiBold, 0, 1)
                    }

                    CoinTossDialog(
                        title = stringResource(id = R.string.new_feature_alert),
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Eight)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.send_custom_coins_message),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(text = stepOne)
                                Text(text = stepTwo)
                                Text(text = stepThreeFull, inlineContent = inlineIconMap)
                            }
                        },
                        onDismiss = onDismiss
                    )
                }
            }
        }

        else -> {}
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
