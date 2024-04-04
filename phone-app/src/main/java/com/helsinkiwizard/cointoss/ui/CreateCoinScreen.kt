package com.helsinkiwizard.cointoss.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
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
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.cointoss.ui.composable.PrimaryOutlinedButton
import com.helsinkiwizard.cointoss.ui.composable.PrimaryTextField
import com.helsinkiwizard.cointoss.ui.composable.dialog.MediaPicker
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.cointoss.ui.model.CustomCoinUiModel
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.DialogState
import com.helsinkiwizard.cointoss.ui.viewmodel.UiState
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.Eighty
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.Sixteen
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.Twenty
import com.helsinkiwizard.core.theme.TwentyFour
import com.helsinkiwizard.core.theme.Two
import com.helsinkiwizard.core.utils.sentenceCase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

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
    val selectedCoin = model.selectedCoin.collectAsState(initial = null).value
    val customCoins = model.customCoins.collectAsState(initial = emptyList()).value

    LazyColumn {
        item {
            val context = LocalContext.current

            AddCoinDetails(
                model = model,
                onHeadsClicked = { viewModel.onCoinSideClicked(CoinSide.HEADS) },
                onTailsClicked = { viewModel.onCoinSideClicked(CoinSide.TAILS) },
                onSaveClicked = {
                    viewModel.saveCoin(
                        storeBitmap = { bitmap -> storeBitmap(context, bitmap) }
                    )
                },
                onClearClicked = { viewModel.clear() },
                onNameChange = { name -> viewModel.onNameChange(name) }
            )
        }
        item {
            SelectedCoin(selectedCoin)
        }
    }
}

@Composable
private fun AddCoinDetails(
    model: CreateCoinModel,
    onHeadsClicked: () -> Unit,
    onTailsClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onClearClicked: () -> Unit,
    onNameChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(TwentyFour),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TwentyFour)
    ) {
        AddCoinImage(
            textRes = R.string.heads,
            bitmap = model.headsBitmap,
            hasError = model.headsError,
            onClick = onHeadsClicked
        )
        AddCoinImage(
            textRes = R.string.tails,
            bitmap = model.tailsBitmap,
            hasError = model.tailsError,
            onClick = onTailsClicked
        )
    }

    PrimaryTextField(
        value = model.name.value,
        onValueChange = onNameChange,
        label = stringResource(id = R.string.name),
        isError = model.name.isError,
        errorText = stringResource(id = R.string.enter_valid_name),
        markOptional = true,
        modifier = Modifier.padding(start = TwentyFour, top = Sixteen, end = TwentyFour)
    )

    AddCoinDetailsButtons(
        onSaveClicked = onSaveClicked,
        onClearClicked = onClearClicked
    )
}

@Composable
private fun RowScope.AddCoinImage(
    textRes: Int,
    bitmap: Bitmap?,
    hasError: Boolean,
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

        val borderColor by animateColorAsState(
            targetValue = when {
                hasError -> MaterialTheme.colorScheme.error
                bitmap != null -> Color.Transparent
                else -> MaterialTheme.colorScheme.surfaceContainerHighest
            },
            label = "Border color"
        )
        val backgroundColor by animateColorAsState(
            targetValue = if (hasError) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
            label = "Background color"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(width = Two, color = borderColor, shape = CircleShape)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = borderColor),
                    onClick = onClick
                )
        ) {
            if (bitmap != null) {
                AsyncImage(
                    model = bitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(Forty)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AddCoinDetailsButtons(
    onSaveClicked: () -> Unit,
    onClearClicked: () -> Unit
) {
    PrimaryButton(
        text = stringResource(id = R.string.save),
        modifier = Modifier.padding(start = Twenty, top = Twenty, end = Twenty, bottom = Twelve),
        onClick = onSaveClicked
    )
    PrimaryOutlinedButton(
        text = stringResource(id = R.string.clear),
        modifier = Modifier.padding(horizontal = Twenty),
        onClick = onClearClicked
    )
}

@Composable
private fun SelectedCoin(selectedCoin: CustomCoinUiModel?) {
    AnimatedVisibility(
        visible = selectedCoin != null,
        enter = slideInVertically() + fadeIn()
    ) {
        if (selectedCoin != null) {
            Column(
                modifier = Modifier.padding(start = Twenty, top = TwentyFour, bottom = TwentyFour)
            ) {
                Text(
                    text = stringResource(id = R.string.selected_custom_coin),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(bottom = Eight)
                        .semantics { heading() }
                )
                CustomCoinItem(coin = selectedCoin)
            }
        }
    }
}

@Composable
private fun CustomCoinItem(coin: CustomCoinUiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
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
            onEditClicked = {},
            onDeleteClicked = {},
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

@Preview(showBackground = true)
@Composable
private fun SelectedCoinPreview() {
    CoinTossTheme {
        Surface {
            val coin = CustomCoinUiModel(1, Uri.EMPTY, Uri.EMPTY, "Name", true)
            SelectedCoin(coin)
        }
    }
}

