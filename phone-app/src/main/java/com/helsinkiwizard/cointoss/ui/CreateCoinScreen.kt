package com.helsinkiwizard.cointoss.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Watch
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.wearable.Wearable
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.ui.composable.dialog.CoinTossDialog
import com.helsinkiwizard.cointoss.ui.composable.dialog.MediaPicker
import com.helsinkiwizard.cointoss.ui.composable.dialog.SelectWatchDialog
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.DialogState
import com.helsinkiwizard.cointoss.ui.viewmodel.UiState
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Eighty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.Sixteen
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.Twenty
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.utils.storeBitmap
import com.helsinkiwizard.core.utils.toBitmap
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val INDEX_ADD_COIN = 0
private const val INDEX_SELECTED_COIN = 1

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
    AsyncImage(
        model = uri,
        contentDescription = "$name, $coinSideString",
        modifier = modifier
            .size(Eighty)
            .clip(shape = CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun IconButtons(
    showSelectButton: Boolean,
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
        IconButton(onClick = onSendToWatchClicked) {
            Image(
                imageVector = Icons.Outlined.Watch,
                contentDescription = "Send to watch",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
        if (showSelectButton) {
            IconButton(onClick = { onSelectClicked?.invoke() }) {
                Image(
                    imageVector = Icons.Outlined.ArrowUpward,
                    contentDescription = stringResource(id = R.string.select),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainerHighest)
                )
            }
        }
        IconButton(onClick = onEditClicked) {
            Image(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(id = R.string.edit),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
        IconButton(onClick = onDeleteClicked) {
            Image(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(id = R.string.delete),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceContainerHighest)
            )
        }
    }
}

fun deleteBitmap(context: Context, uri: Uri): Boolean {
    return try {
        val deletedRows = context.contentResolver.delete(uri, null, null)
        // If delete operation was successful, it returns the number of rows deleted.
        // In case of a file, it should be 1 if the file was successfully deleted.
        deletedRows > 0
    } catch (e: Exception) {
        Log.e("DeleteFile", "Failed to delete file", e)
        false
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
    CoinTossTheme {
        Surface {
            Content(model, viewModel)
        }
    }
}
