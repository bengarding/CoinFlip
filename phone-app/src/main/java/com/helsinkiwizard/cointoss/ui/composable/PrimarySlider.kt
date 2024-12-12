package com.helsinkiwizard.cointoss.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.core.CoreConstants.SPEED_MAX
import com.helsinkiwizard.core.CoreConstants.SPEED_MIN
import com.helsinkiwizard.core.CoreConstants.SPEED_STEPS
import com.helsinkiwizard.core.theme.Eight

private const val NO_STEPS = 0
private const val DEFAULT_DECIMAL_PLACES = 1

@Composable
fun PrimarySlider(
    value: Float,
    minRange: Float,
    maxRange: Float,
    title: String,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    decimalPlaces: Int = DEFAULT_DECIMAL_PLACES,
    steps: Int = NO_STEPS,
) {
    Column(modifier = modifier) {
        Row {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = Eight)
            )
            Text(
                text = String.format("%.${decimalPlaces}f", value),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Eight)
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = minRange..maxRange,
            steps = steps,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimarySliderPreview() {
    PreviewSurface {
        var value by remember { mutableFloatStateOf(3f) }
        PrimarySlider(
            value = value,
            minRange = SPEED_MIN,
            maxRange = SPEED_MAX,
            steps = SPEED_STEPS,
            title = "Speed (seconds)",
            onValueChange = { newValue -> value = newValue },
            onValueChangeFinished = {}
        )
    }
}
