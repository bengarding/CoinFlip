package com.helsinkiwizard.cointoss.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.core.theme.Text14
import com.helsinkiwizard.core.theme.Text16
import com.helsinkiwizard.core.theme.Text20
import com.helsinkiwizard.core.theme.Text22

val ArimaMadurai = FontFamily(
    Font(R.font.arima_madurai)
)

val Mulish = FontFamily(
    Font(R.font.mulish)
)

val Typography = Typography(
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
    titleLarge = TextStyle(
        fontFamily = ArimaMadurai,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text22
    ),
    titleMedium = TextStyle(
        fontFamily = ArimaMadurai,
        fontWeight = FontWeight.SemiBold,
        fontSize = Text20
    )
)
