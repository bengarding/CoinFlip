package com.helsinkiwizard.cointoss.ui

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.google.android.gms.wearable.Wearable
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.ui.composable.ErrorScreen
import com.helsinkiwizard.cointoss.ui.composable.PreviewSurface
import com.helsinkiwizard.cointoss.ui.composable.ProgressIndicator
import com.helsinkiwizard.cointoss.ui.composable.dialog.CoinTossDialog
import com.helsinkiwizard.cointoss.ui.composable.dialog.MediaPicker
import com.helsinkiwizard.cointoss.ui.composable.dialog.SelectWatchDialog
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinError
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinViewModel
import com.helsinkiwizard.cointoss.utils.launchInAppReview
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Eighty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.LocalActivity
import com.helsinkiwizard.core.theme.Sixteen
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.Twenty
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.utils.deleteBitmap
import com.helsinkiwizard.core.utils.storeBitmap
import com.helsinkiwizard.core.utils.toBitmap
import com.helsinkiwizard.core.viewmodel.DialogState
import com.helsinkiwizard.core.viewmodel.UiState
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val INDEX_ADD_COIN = 0
private const val INDEX_SELECTED_COIN = 1

@Composable
fun CreateCoinScreen(
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

        is UiState.Loading -> {
            ProgressIndicator()
        }

        is UiState.Error -> {
            var messageRes: Int = R.string.error_sending_coin_to_watch
            var retry: (() -> Unit)? = null
            when (val type = state.type as? CreateCoinError) {
                is CreateCoinError.SendToWatchError -> {
                    messageRes = type.messageRes
                    retry = type.retry
                }

                else -> {}
            }
            ErrorScreen(
                message = stringResource(id = messageRes),
                onCancelClicked = { viewModel.showContent() },
                onRetryClicked = retry
            )
        }
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
                        onImageCropped = { bitmap -> viewModel.setBitmap(bitmap, type.coinSide) }
                    )
                }

                is CreateCoinDialogs.MissingImages -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.add_both_images_to_save),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetDialogState()
                }

                is CreateCoinDialogs.SaveSuccess -> {
                    LocalActivity.current.launchInAppReview()
                }

                is CreateCoinDialogs.SaveError -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.save_error),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetDialogState()
                }

                is CreateCoinDialogs.DeleteCoinDialog -> {
                    CoinTossDialog(
                        text = stringResource(id = R.string.are_you_sure_delete_coin),
                        confirmButtonText = stringResource(id = R.string.delete),
                        dismissButtonText = stringResource(id = R.string.cancel),
                        onConfirmButtonClick = { viewModel.deleteCoin(type.coin) },
                        onDismiss = { viewModel.resetDialogState() },
                    )
                }

                is CreateCoinDialogs.DeleteCoinBitmaps -> {
                    deleteBitmap(LocalContext.current, type.headsUri)
                    deleteBitmap(LocalContext.current, type.tailsUri)
                    viewModel.resetDialogState()
                }

                is CreateCoinDialogs.SendToWatchSuccess -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.success),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetDialogState()
                }

                is CreateCoinDialogs.NoNodesFoundDialog -> {
                    CoinTossDialog(
                        title = stringResource(id = R.string.watch_not_found),
                        text = stringResource(id = R.string.watch_not_found_message),
                        onDismiss = { viewModel.resetDialogState() }
                    )
                }

                is CreateCoinDialogs.SelectNodesDialog -> {
                    SelectWatchDialog(
                        nodes = type.nodes,
                        onDismiss = { viewModel.resetDialogState() },
                        onNodeClick = { selectedNodeId ->
                            viewModel.sendCoinToNode(
                                coin = type.coin,
                                node = type.nodes.first { it.id == selectedNodeId },
                                channelClient = type.channelClient,
                                messageClient = type.messageClient,
                                uriToBitmap = type.uriToBitmap
                            )
                        }
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
    viewModel: CreateCoinViewModel
) {
    val context = LocalContext.current
    val selectedCoin = model.selectedCoin.collectAsState(initial = null).value
    val customCoins = model.customCoins.collectAsState(initial = emptyList()).value
    val showSendToWatchButton = model.showSendToWatchButton.collectAsState(initial = false).value
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messageClient by lazy { Wearable.getMessageClient(context) }
    val capabilityClient by lazy { Wearable.getCapabilityClient(context) }
    val channelClient by lazy { Wearable.getChannelClient(context) }

    LazyColumn(state = listState) {
        item {
            val keyboardController = LocalSoftwareKeyboardController.current

            AddCoinDetails(
                model = model,
                onHeadsClicked = { viewModel.onCoinSideClicked(CoinSide.HEADS) },
                onTailsClicked = { viewModel.onCoinSideClicked(CoinSide.TAILS) },
                onSaveClicked = {
                    keyboardController?.hide()
                    viewModel.saveCoin(
                        storeBitmap = { bitmap -> storeBitmap(context, bitmap) }
                    )
                },
                onClearClicked = { viewModel.clear() },
                onNameChange = { name -> viewModel.onNameChange(name) },
                isEditing = model.isEditing
            )
        }
        item(key = selectedCoin?.id) {
            SelectedCoin(
                selectedCoin = selectedCoin,
                showSendToWatchButton = showSendToWatchButton,
                onEditClicked = {
                    viewModel.onEditClicked(
                        coin = selectedCoin!!,
                        uriToBitmap = { it.toBitmap(context) }
                    )
                    scope.launch {
                        listState.animateScrollToItem(INDEX_ADD_COIN)
                    }
                },
                onDeleteClicked = { viewModel.onDeleteClicked(selectedCoin!!) },
                onSendToWatchClicked = {
                    viewModel.sendCoinToWatch(
                        coin = selectedCoin!!,
                        messageClient = messageClient,
                        capabilityClient = capabilityClient,
                        channelClient = channelClient,
                        uriToBitmap = { it.toBitmap(context) }
                    )
                }
            )
        }
        item {
            if (customCoins.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.custom_coins),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = Twenty)
                        .semantics { heading() }
                )
            }
        }
        itemsIndexed(items = customCoins) { index, customCoin ->
            val showDivider = index != customCoins.size - 1
            CustomCoinItem(
                coin = customCoin,
                showDivider = showDivider,
                showSelectButton = true,
                showSendToWatchButton = showSendToWatchButton,
                onEditClicked = {
                    viewModel.onEditClicked(
                        coin = customCoin,
                        uriToBitmap = { it.toBitmap(context) }
                    )
                    scope.launch {
                        listState.animateScrollToItem(INDEX_ADD_COIN)
                    }
                },
                onDeleteClicked = { viewModel.onDeleteClicked(customCoin) },
                onSelectClicked = { coin ->
                    viewModel.setSelectedCoin(coin)
                    scope.launch {
                        listState.animateScrollToItem(INDEX_SELECTED_COIN)
                    }
                },
                onSendToWatchClicked = {
                    viewModel.sendCoinToWatch(
                        coin = customCoin,
                        messageClient = messageClient,
                        capabilityClient = capabilityClient,
                        channelClient = channelClient,
                        uriToBitmap = { it.toBitmap(context) }
                    )
                }
            )
        }
    }
}

@Composable
private fun SelectedCoin(
    selectedCoin: CustomCoinUiModel?,
    showSendToWatchButton: Boolean,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onSendToWatchClicked: () -> Unit,
) {
    if (selectedCoin != null) {
        Column(
            modifier = Modifier.padding(top = Twelve, bottom = Sixteen)
        ) {
            Text(
                text = stringResource(id = R.string.selected_custom_coin),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = Twenty)
                    .semantics { heading() }
            )
            CustomCoinItem(
                coin = selectedCoin,
                showSendToWatchButton = showSendToWatchButton,
                onEditClicked = onEditClicked,
                onDeleteClicked = onDeleteClicked,
                onSendToWatchClicked = onSendToWatchClicked
            )
        }
    }
}

@Composable
private fun CustomCoinItem(
    coin: CustomCoinUiModel,
    showDivider: Boolean = false,
    showSelectButton: Boolean = false,
    showSendToWatchButton: Boolean,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onSendToWatchClicked: () -> Unit,
    onSelectClicked: ((CustomCoinUiModel) -> Unit)? = null
) {
    Column {
        Column(
            modifier = Modifier.padding(start = Twenty, top = Eight, bottom = Eight)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomCoinSide(
                    uri = coin.headsUri,
                    name = coin.name,
                    coinSideString = stringResource(id = R.string.heads),
                    modifier = Modifier.padding(end = Eight)
                )
                CustomCoinSide(
                    uri = coin.tailsUri,
                    name = coin.name,
                    coinSideString = stringResource(id = R.string.tails),
                    modifier = Modifier.padding()
                )
                IconButtons(
                    showSelectButton = showSelectButton,
                    showSendToWatchButton = showSendToWatchButton,
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked,
                    onSendToWatchClicked = onSendToWatchClicked,
                    onSelectClicked = { onSelectClicked?.invoke(coin) },
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = coin.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = Eight, top = Four)
            )
        }
        if (showDivider) {
            HorizontalDivider()
        }
    }
}

@Composable
private fun CustomCoinSide(
    uri: Uri,
    name: String,
    coinSideString: String,
    modifier: Modifier = Modifier
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(uri)
        .size(Size.ORIGINAL)
        .build()

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = "$name, $coinSideString",
        modifier = modifier
            .size(Eighty)
            .clip(shape = CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        error = {
            Icon(
                imageVector = Icons.Outlined.BrokenImage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(Twenty)
            )
        }
    )
}

@Composable
private fun IconButtons(
    showSelectButton: Boolean,
    showSendToWatchButton: Boolean,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onSendToWatchClicked: () -> Unit,
    onSelectClicked: (() -> Unit)? = null,
    modifier: Modifier
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
    ) {
        if (showSendToWatchButton) {
            IconButton(onClick = onSendToWatchClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send_to_watch),
                    contentDescription = stringResource(id = R.string.send_to_watch),
                    tint = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            }
        }
        if (showSelectButton) {
            IconButton(onClick = { onSelectClicked?.invoke() }) {
                Icon(
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = stringResource(id = R.string.select),
                    tint = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            }
        }
        IconButton(onClick = onEditClicked) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(id = R.string.edit),
                tint = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
        IconButton(onClick = onDeleteClicked) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(id = R.string.delete),
                tint = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateCoinScreenPreview() {
    val model = CreateCoinModel(
        selectedCoin = flowOf(CustomCoinUiModel(1, Uri.EMPTY, Uri.EMPTY, "Name")),
        customCoins = flowOf(
            listOf(
                CustomCoinUiModel(2, Uri.EMPTY, Uri.EMPTY, "Second"),
                CustomCoinUiModel(3, Uri.EMPTY, Uri.EMPTY, "Third")
            )
        )
    )
    val repository = Repository(LocalContext.current)
    val viewModel = CreateCoinViewModel(repository)
    PreviewSurface {
        Content(model, viewModel)
    }
}
