package com.helsinkiwizard.cointoss.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.Eighty
import com.helsinkiwizard.core.theme.Sixteen
import com.helsinkiwizard.core.theme.ThirtyTwo
import com.helsinkiwizard.core.theme.TwentyFour

private const val IMAGE_WIDTH_PERCENT = .8f

@Composable
fun ErrorScreen(
    message: String,
    onCancelClicked: () -> Unit,
    onRetryClicked: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = TwentyFour, bottom = Eighty, end = TwentyFour)
    ) {
        Image(
            painter = painterResource(id = R.drawable.broken_coin),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(IMAGE_WIDTH_PERCENT)
        )
        Text(
            text = stringResource(id = R.string.something_went_wrong),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = ThirtyTwo)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = TwentyFour)
        )
        PrimaryOutlinedButton(
            text = stringResource(id = R.string.cancel),
            modifier = Modifier.padding(top = ThirtyTwo),
            onClick = onCancelClicked
        )
        if (onRetryClicked != null) {
            PrimaryButton(
                text = stringResource(id = R.string.retry),
                modifier = Modifier.padding(top = Sixteen),
                onClick = onRetryClicked
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    Surface {
        CoinTossTheme {
            ErrorScreen(
                message = stringResource(id = R.string.error_sending_coin_to_watch),
                onCancelClicked = {},
                onRetryClicked = {}
            )
        }
    }
}
