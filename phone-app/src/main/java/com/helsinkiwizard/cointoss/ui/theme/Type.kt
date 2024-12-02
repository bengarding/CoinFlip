package com.helsinkiwizard.cointoss.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.core.theme.Text14
import com.helsinkiwizard.core.theme.Text16
import com.helsinkiwizard.core.theme.Text20
import com.helsinkiwizard.core.theme.Text22
import com.helsinkiwizard.core.theme.Text24
import com.helsinkiwizard.core.theme.Text60

val ArimaMadurai = FontFamily(
    Font(R.font.arima_madurai)
)

val Mulish = FontFamily(
    Font(R.font.mulish)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text22
    ),
    titleMedium = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text20
    ),
    titleSmall = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text16
    ),
    displayLarge = TextStyle(
        fontFamily = ArimaMadurai,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text24
    ),
    displayMedium = TextStyle(
        fontFamily = ArimaMadurai,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text20
    ),
    // displaySmall is used for revenuecat's dialog, which is why the font size is so large
    displaySmall = TextStyle(
        fontFamily = ArimaMadurai,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text60
    ),
    bodyLarge = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Normal,
        fontSize = Text20
    ),
    bodyMedium = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Normal,
        fontSize = Text16
    ),
    bodySmall = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Normal,
        fontSize = Text14
    ),
)

val BodyMediumSpan: SpanStyle
    @Composable
    get() = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
        color = MaterialTheme.colorScheme.onBackground
    )

val LinkText: SpanStyle
    @Composable
    get() = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        textDecoration = TextDecoration.Underline
    )
