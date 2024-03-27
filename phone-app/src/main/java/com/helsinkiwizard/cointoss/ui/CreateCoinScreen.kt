package com.helsinkiwizard.cointoss.ui

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.composable.dialog.MediaPicker
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.DialogState
import com.helsinkiwizard.cointoss.ui.viewmodel.UiState
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.TwentyFour
import com.helsinkiwizard.core.theme.Two
import com.helsinkiwizard.core.utils.sentenceCase

@Composable
fun CreateCoinScreen(
    navController: NavController,
    viewModel: CreateCoinViewModel = hiltViewModel()
) {
    CreateCoinContent(viewModel)
    CreateCoinDialogs(viewModel)
}

@Composable
private fun CreateCoinContent(viewModel: CreateCoinViewModel) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is UiState.ShowContent -> {
            when (val type = state.type as CreateCoinContent) {
                is CreateCoinContent.LoadingComplete -> Content(type.model, viewModel)
            }
        }

        else -> {}
    }
}

@Composable
private fun CreateCoinDialogs(viewModel: CreateCoinViewModel) {
    when (val state = viewModel.dialogState.collectAsState().value) {
        is DialogState.ShowContent -> {
            when (val type = state.type as CreateCoinDialogs) {
                is CreateCoinDialogs.MediaPicker -> {
                    MediaPicker(
                        onDismiss = { viewModel.resetDialogState() },
                        imageSelected = { uri -> viewModel.setUri(uri, type.coinSide) }
                    )
                }
            }
        }

        else -> {}
    }
}

@Composable
private fun Content(
    model: CreateCoinModel,
    viewModel: CreateCoinViewModel,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(TwentyFour),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TwentyFour)

    ) {
        CoinImage(
            textRes = R.string.heads,
            headsUri = model.headsUri,
            onClick = { viewModel.onCoinSideClicked(CoinSide.HEADS) }
        )
        CoinImage(
            textRes = R.string.tails,
            headsUri = model.tailsUri,
            onClick = { viewModel.onCoinSideClicked(CoinSide.TAILS) }
        )
    }
}

@Composable
private fun RowScope.CoinImage(
    textRes: Int,
    headsUri: Uri?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = textRes).sentenceCase(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = Twelve, top = Twelve, end = Twelve, bottom = Twelve)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(
                    width = Two,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = CircleShape
                )
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = MaterialTheme.colorScheme.surfaceContainerHighest),
                    onClick = onClick
                )
        ) {
            if (headsUri != null) {
                AsyncImage(
                    model = headsUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
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
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateCoinScreenPreview() {
    val model = CreateCoinModel()
    val repository = Repository(LocalContext.current)
    val viewModel = CreateCoinViewModel(repository)
    CoinTossTheme {
        Surface {
            Content(model, viewModel)
        }
    }
}
