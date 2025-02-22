package com.helsinkiwizard.cointoss.ui.composable.dialog

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.scale
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.composable.PreviewSurface
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.core.theme.DialogTonalOverlay
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.Twenty
import timber.log.Timber

private const val MAX_IMAGE_SIZE = 1000

@Composable
fun MediaPicker(
    onDismiss: () -> Unit,
    onImageCropped: (Bitmap) -> Unit
) {
    var openDialog by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(contract = CropImageContract()) { result ->
        openDialog = false
        onDismiss()

        if (result.isSuccessful) {
            result.uriContent?.let {
                //getBitmap method is deprecated in Android SDK 29 or above so we need to do this check here
                var bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }

                // Scale down the image if it's very large
                if (bitmap.width > MAX_IMAGE_SIZE || bitmap.height > MAX_IMAGE_SIZE) {
                    bitmap = bitmap.scale(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
                }

                onImageCropped(bitmap)
            }
        } else {
            Timber.e(result.error, "Image crop error")
        }
    }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
                onDismiss.invoke()
            },
            confirmButton = {},
            tonalElevation = DialogTonalOverlay,
            title = {
                Text(
                    text = stringResource(id = R.string.get_picture_from),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Twelve)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Twenty)
                ) {
                    val cropImageOptions = CropImageOptions(
                        cropShape = CropImageView.CropShape.OVAL,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        cropMenuCropButtonTitle = "Crop menu",
                        cropperLabelText = "cropper label",
                        cropMenuCropButtonIcon = R.drawable.ic_done,
                        guidelines = CropImageView.Guidelines.OFF,
                        progressBarColor = MaterialTheme.colorScheme.primary.toArgb(),
                        toolbarColor = MaterialTheme.colorScheme.primary.toArgb(),
                        toolbarBackButtonColor = MaterialTheme.colorScheme.onPrimary.toArgb(),
                        activityMenuIconColor = MaterialTheme.colorScheme.onPrimary.toArgb(),
                        activityBackgroundColor = MaterialTheme.colorScheme.background.toArgb()
                    )
                    PrimaryButton(
                        icon = Icons.Outlined.CameraAlt,
                        text = stringResource(id = R.string.camera),
                        onClick = {
                            val cropOptions = CropImageContractOptions(
                                uri = null,
                                cropImageOptions = cropImageOptions.apply {
                                    imageSourceIncludeGallery = false
                                }
                            )
                            imageCropLauncher.launch(cropOptions)
                        }
                    )
                    PrimaryButton(
                        icon = Icons.Outlined.Image,
                        text = stringResource(id = R.string.gallery),
                        onClick = {
                            val cropOptions = CropImageContractOptions(
                                uri = null,
                                cropImageOptions = cropImageOptions.apply {
                                    imageSourceIncludeCamera = false
                                }
                            )
                            imageCropLauncher.launch(cropOptions)
                        }
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaPickerPreview() {
    PreviewSurface {
        MediaPicker({}, {})
    }
}
