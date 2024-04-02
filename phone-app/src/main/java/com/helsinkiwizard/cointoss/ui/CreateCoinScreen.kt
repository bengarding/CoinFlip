package com.helsinkiwizard.cointoss.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.cointoss.ui.composable.PrimaryOutlinedButton
import com.helsinkiwizard.cointoss.ui.composable.dialog.MediaPicker
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinContent
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinDialogs
import com.helsinkiwizard.cointoss.ui.viewmodel.CreateCoinViewModel
import com.helsinkiwizard.cointoss.ui.viewmodel.DialogState
import com.helsinkiwizard.cointoss.ui.viewmodel.UiState
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.theme.Forty
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
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(TwentyFour),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TwentyFour)
        ) {
            CoinImage(
                textRes = R.string.heads,
                bitmap = model.headsBitmap,
                hasError = model.headsError,
                onClick = { viewModel.onCoinSideClicked(CoinSide.HEADS) }
            )
            CoinImage(
                textRes = R.string.tails,
                bitmap = model.tailsBitmap,
                hasError = model.tailsError,
                onClick = { viewModel.onCoinSideClicked(CoinSide.TAILS) }
            )
        }
        val context = LocalContext.current
        Buttons(
            onSaveClicked = {
                viewModel.saveCoin(
                    storeBitmap = { bitmap -> storeBitmap(context, bitmap) }
                )
            },
            onClearClicked = { viewModel.clear() }
        )
    }
}

@Composable
private fun RowScope.CoinImage(
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(width = Two, color = borderColor, shape = CircleShape)
                .clip(CircleShape)
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
private fun Buttons(
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
