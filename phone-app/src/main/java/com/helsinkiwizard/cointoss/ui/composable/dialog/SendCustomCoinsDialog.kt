package com.helsinkiwizard.cointoss.ui.composable.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.cointoss.ui.composable.PrimaryOutlinedButton
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.DialogTonalOverlay
import com.helsinkiwizard.core.theme.Four
import com.helsinkiwizard.core.theme.Twelve

private const val SEND_TO_WATCH_ICON_ID = "send_to_watch"

private val inlineIconMap = mapOf(
    SEND_TO_WATCH_ICON_ID to InlineTextContent(
        Placeholder(30.sp, 30.sp, PlaceholderVerticalAlign.TextCenter)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_send_to_watch),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Four)
        )
    }
)

@Composable
fun SendCustomCoinsDialog(
    onDismiss: () -> Unit,
    onHideButtonClicked: () -> Unit
) {
    var openDialog by remember { mutableStateOf(true) }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                openDialog = false
                onDismiss.invoke()
            },
            confirmButton = { },
            tonalElevation = DialogTonalOverlay,
            title = {
                Text(
                    text = stringResource(id = R.string.new_feature_alert),
                    style = MaterialTheme.typography.displayMedium
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Twelve),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    val bodyMedium = MaterialTheme.typography.bodyMedium.toSpanStyle()
                    val bodyMediumSemiBold = bodyMedium.copy(fontWeight = FontWeight.SemiBold)

                    val stepOne = buildAnnotatedString {
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_1))
                        }
                        addStyle(bodyMediumSemiBold, 0, 1)
                    }

                    val stepTwo = buildAnnotatedString {
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_2))
                        }
                        addStyle(bodyMediumSemiBold, 0, 1)
                    }

                    val stepThreeFull = buildAnnotatedString {
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_3_start))
                        }
                        appendInlineContent(SEND_TO_WATCH_ICON_ID)
                        withStyle(bodyMedium) {
                            append(stringResource(id = R.string.send_custom_coins_step_3_end))
                        }
                        addStyle(bodyMediumSemiBold, 0, 1)
                    }

                    Text(
                        text = stringResource(id = R.string.send_custom_coins_message),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = Twelve)
                    )
                    Text(text = stepOne)
                    Text(text = stepTwo)
                    Text(text = stepThreeFull, inlineContent = inlineIconMap)
                    PrimaryOutlinedButton(
                        text = stringResource(id = R.string.send_custom_coins_i_dont_have_watch),
                        modifier = Modifier.padding(top = Twelve),
                        onClick = {
                            onHideButtonClicked.invoke()
                            onDismiss.invoke()
                        }
                    )
                    PrimaryButton(
                        text = stringResource(id = R.string.send_custom_coins_got_it),
                        onClick = onDismiss
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SendCustomCoinsDialogPreview() {
    CoinTossTheme {
        Surface {
            SendCustomCoinsDialog(
                onDismiss = {},
                onHideButtonClicked = {}
            )
        }
    }
}


