package com.helsinkiwizard.cointoss.ui.composable.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.DialogTonalOverlay
import com.helsinkiwizard.core.theme.Twenty

@Composable
fun CoinTossDialog(
    onDismiss: () -> Unit,
    text: String,
    title: String? = null,
    confirmButtonText: String = stringResource(id = R.string.ok),
    dismissButtonText: String? = null,
    onConfirmButtonClick: (() -> Unit)? = null,
    onDismissButtonClick: (() -> Unit)? = null,
) {
    var openDialog by remember { mutableStateOf(true) }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
                onDismiss.invoke()
            },
            confirmButton = {
                Text(
                    text = confirmButtonText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable {
                            onConfirmButtonClick?.invoke()
                            onDismiss.invoke()
                            openDialog = false
                        }
                )
            },
            dismissButton = {
                if (dismissButtonText != null) {
                    Text(
                        text = dismissButtonText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier
                            .padding(end = Twenty)
                            .minimumInteractiveComponentSize()
                            .clickable {
                                onDismissButtonClick?.invoke()
                                onDismiss.invoke()
                                openDialog = false
                            }
                    )
                }
            },
            tonalElevation = DialogTonalOverlay,
            title = {
                if (title != null) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            },
            text = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinTossDialogPreview() {
    CoinTossTheme {
        Surface {
            CoinTossDialog(
                onDismiss = { },
                title = "This is a title",
                text = stringResource(id = R.string.are_you_sure_delete_coin),
                confirmButtonText = "Delete",
                dismissButtonText = "Cancel"
            )
        }
    }
}
