package com.helsinkiwizard.cointoss.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.ui.composable.dialog.CoinTossDialog
import com.helsinkiwizard.cointoss.ui.composable.dialog.MediaPicker
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.cointoss.ui.model.CustomCoinUiModel
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.DialogState
import com.helsinkiwizard.cointoss.ui.viewmodel.UiState
import com.helsinkiwizard.cointoss.utils.toBitmap
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Eighty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.Sixteen
import com.helsinkiwizard.core.theme.Twenty
import com.helsinkiwizard.core.theme.TwentyFour
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

private const val FIRST_ITEM = 0

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
                onNameChange = { name -> viewModel.onNameChange(name) }
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
                        listState.animateScrollToItem(FIRST_ITEM)
                    }
                },
                onDeleteClicked = { viewModel.onDeleteClicked(selectedCoin!!) },
            )
        }
        item {
            Text(
                text = stringResource(id = R.string.custom_coins),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = Twenty)
                    .semantics { heading() }
            )
        }
        items(items = customCoins, key = { it.id }) { customCoin ->
            CustomCoinItem(
                coin = customCoin,
                showDivider = true,
                onEditClicked = {
                    viewModel.onEditClicked(
                        coin = customCoin,
                        uriToBitmap = { it.toBitmap(context) }
                    )
                    scope.launch {
                        listState.animateScrollToItem(FIRST_ITEM)
                    }
                },
                onDeleteClicked = { viewModel.onDeleteClicked(customCoin) },
            )
        }
    }
}

@Composable
private fun SelectedCoin(
    selectedCoin: CustomCoinUiModel?,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
) {
    if (selectedCoin != null) {
        Column(
            modifier = Modifier.padding(top = TwentyFour, bottom = Sixteen)
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
            )
        }
    }
}

@Composable
private fun CustomCoinItem(
    coin: CustomCoinUiModel,
    showDivider: Boolean = false,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
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
                    onEditClicked = onEditClicked,
                    onDeleteClicked = onDeleteClicked,
                    modifier = Modifier.weight(1f)
                )
            }
            if (coin.name.isNotEmpty()) {
                Text(
                    text = coin.name,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = Eight, top = Four)
                )
            }
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
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    modifier: Modifier
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = modifier
    ) {
        IconButton(onClick = onDeleteClicked) {
            Image(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
        IconButton(onClick = onEditClicked) {
            Image(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

private fun storeBitmap(context: Context, bitmap: Bitmap?): Uri? {
    val compressFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Bitmap.CompressFormat.WEBP_LOSSY
    } else {
        Bitmap.CompressFormat.JPEG
    }
    val fileType = if (compressFormat == Bitmap.CompressFormat.JPEG) "jpg" else "webp"
    val imageName = Random.nextInt()
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$imageName.$fileType")

    return try {
        FileOutputStream(file).use { out ->
            bitmap?.compress(compressFormat, 100, out)
        }
        FileProvider.getUriForFile(context, "${context.packageName}.file-provider", file)
    } catch (e: IOException) {
        Log.e("CreateCoinScreen", "storeBitmap", e)
        null
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
        selectedCoin = flowOf(CustomCoinUiModel(1, Uri.EMPTY, Uri.EMPTY, "Name", true)),
        customCoins = flowOf(
            listOf(
                CustomCoinUiModel(2, Uri.EMPTY, Uri.EMPTY, "Second", false),
                CustomCoinUiModel(3, Uri.EMPTY, Uri.EMPTY, "Third", false)
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
