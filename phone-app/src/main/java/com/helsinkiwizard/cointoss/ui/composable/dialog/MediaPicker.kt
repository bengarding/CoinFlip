package com.helsinkiwizard.cointoss.ui.composable.dialog

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.cointoss.Constants.MIME_TYPE_IMAGE
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.theme.CoinTossTheme
import com.helsinkiwizard.cointoss.ui.ImageCropActivity
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.cointoss.utils.parcelable
import com.helsinkiwizard.core.theme.DialogTonalOverlay
import com.helsinkiwizard.core.theme.Twenty

@Composable
fun MediaPicker(
    onDismiss: () -> Unit,
    imageSelected: (Uri) -> Unit
) {
    var openDialog by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val imageCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            val uri = it.data?.extras?.parcelable<Uri>(ImageCropActivity.EXTRA_URI)
            if (uri != null) {
                imageSelected(uri)
            }
        }
    )
    val galleryLauncher = rememberLauncherForActivityResult(
        onResult = { uri ->
            if (uri != null) {
                imageCropLauncher.launch(ImageCropActivity.createIntent(context, uri))
            }
            onDismiss()
        },
        contract = ActivityResultContracts.GetContent()
    )

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
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Twenty)
                ) {
                    PrimaryButton(
                        icon = Icons.Outlined.CameraAlt,
                        text = stringResource(id = R.string.camera),
                        onClick = {}
                    )
                    PrimaryButton(
                        icon = Icons.Outlined.Image,
                        text = stringResource(id = R.string.gallery),
                        onClick = { galleryLauncher.launch(MIME_TYPE_IMAGE) }
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaPickerPreview() {
    CoinTossTheme {
        Surface {
            MediaPicker({}, {})
        }
    }
}
