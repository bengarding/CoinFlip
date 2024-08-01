package com.helsinkiwizard.cointoss.ui.composable.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.android.gms.wearable.Node
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.DialogTonalOverlay
import com.helsinkiwizard.core.theme.Twelve

@Composable
fun SelectWatchDialog(
    nodes: Set<Node>,
    onDismiss: () -> Unit,
    onNodeClick: (String) -> Unit
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
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable {
                            onDismiss.invoke()
                            openDialog = false
                        }
                )
            },
            tonalElevation = DialogTonalOverlay,
            title = {
                Text(
                    text = stringResource(id = R.string.select_watch),
                    style = MaterialTheme.typography.displayMedium
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Twelve)
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.select_watch_description),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = Twelve)
                        )
                    }
                    items(nodes.toList()) { node ->
                        PrimaryButton(
                            text = node.displayName,
                            onClick = {
                                onNodeClick(node.id)
                                openDialog = false
                                onDismiss()
                            }
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CoinTossDialogPreview() {
    class PreviewNode(
        val nodeName: String,
        val nodeId: String,
    ) : Node {
        override fun getDisplayName() = nodeName
        override fun getId() = nodeId
        override fun isNearby() = true
    }

    CoinTossTheme {
        Surface {
            SelectWatchDialog(
                nodes = setOf(
                    PreviewNode("Galaxy Watch 5", "1"),
                    PreviewNode("Pixel Watch 2", "2"),
                    PreviewNode("Galaxy Watch 6", "3"),
                ),
                onDismiss = {},
                onNodeClick = {}
            )
        }
    }
}


