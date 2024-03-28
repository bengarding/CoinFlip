package com.helsinkiwizard.cointoss.ui.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.cointoss.data.ThemeMode
import com.helsinkiwizard.cointoss.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.ButtonHeight
import com.helsinkiwizard.core.theme.Eight
import com.helsinkiwizard.core.theme.One
import com.helsinkiwizard.core.theme.Thirty
import com.helsinkiwizard.core.theme.Twenty

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(ButtonHeight),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.size(Thirty)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PrimaryOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(ButtonHeight),
        border = BorderStroke(width = One, color = MaterialTheme.colorScheme.onSecondaryContainer),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null) {
                Image(
                    imageVector = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier.size(Thirty)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonPreview() {
    CoinTossTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(Twenty),
                modifier = Modifier.padding(Eight)
            ) {
                PrimaryButton(
                    text = "Rate on Google Play",
                    onClick = {}
                )
                PrimaryButton(
                    text = "Gallery",
                    icon = Icons.Outlined.Image,
                    onClick = {}
                )
                PrimaryOutlinedButton(
                    text = "Clear",
                    onClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryButtonDarkPreview() {
    CoinTossTheme(
        themeMode = ThemeMode.DARK
    ) {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(Twenty),
                modifier = Modifier.padding(Eight)
            ) {
                PrimaryButton(
                    text = "Rate on Google Play",
                    onClick = {}
                )
                PrimaryButton(
                    text = "Gallery",
                    icon = Icons.Outlined.Image,
                    onClick = {}
                )
                PrimaryOutlinedButton(
                    text = "Clear",
                    onClick = {}
                )
            }
        }
    }
}