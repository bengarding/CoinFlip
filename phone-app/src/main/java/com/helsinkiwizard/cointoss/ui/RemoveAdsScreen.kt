package com.helsinkiwizard.cointoss.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.theme.CoinTossTheme

@Composable
fun RemoveAdsScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_no_ads),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    Surface {
        CoinTossTheme {
            RemoveAdsScreen()
        }
    }
}
