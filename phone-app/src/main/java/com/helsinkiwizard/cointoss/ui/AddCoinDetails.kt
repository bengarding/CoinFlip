package com.helsinkiwizard.cointoss.ui

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.ui.composable.PrimaryButton
import com.helsinkiwizard.cointoss.ui.composable.PrimaryOutlinedButton
import com.helsinkiwizard.cointoss.ui.composable.PrimaryTextField
import com.helsinkiwizard.cointoss.ui.composable.additionalLayoutSize
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.core.CoreConstants.VALUE_UNDEFINED
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Sixteen
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.Twenty
import com.helsinkiwizard.core.theme.TwentyFour
import com.helsinkiwizard.core.theme.Two
import com.helsinkiwizard.core.utils.sentenceCase

@Composable
internal fun AddCoinDetails(
    model: CreateCoinModel,
    onHeadsClicked: () -> Unit,
    onTailsClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onClearClicked: () -> Unit,
    onNameChange: (String) -> Unit,
    isEditing: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(TwentyFour),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TwentyFour)
    ) {
        AddCoinImage(
            textRes = R.string.heads,
            bitmap = model.headsBitmap,
            hasError = model.headsError,
            onClick = onHeadsClicked
        )
        AddCoinImage(
            textRes = R.string.tails,
            bitmap = model.tailsBitmap,
            hasError = model.tailsError,
            onClick = onTailsClicked
        )
    }

    val density = LocalDensity.current
    var heightWithOffset by remember { mutableIntStateOf(VALUE_UNDEFINED) }

    PrimaryTextField(
        value = model.name.value,
        onValueChange = onNameChange,
        label = stringResource(id = R.string.name),
        isError = model.name.isError,
        errorText = stringResource(id = R.string.enter_valid_name),
        markOptional = true,
        modifier = Modifier
            .padding(start = TwentyFour, top = Sixteen, end = TwentyFour)
            .additionalLayoutSize(
                additionalHeight = with(density) { Two.roundToPx() },
                heightWithOffset = heightWithOffset,
                updateHeightWithOffset = { heightWithOffset = it }
            )
    )

    AddCoinDetailsButtons(
        onSaveClicked = onSaveClicked,
        onClearClicked = onClearClicked,
        isEditing = isEditing
    )
}

@Composable
private fun RowScope.AddCoinImage(
    textRes: Int,
    bitmap: Bitmap?,
    hasError: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = textRes).sentenceCase(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = Twelve, top = Twelve, end = Twelve, bottom = Twelve)
        )

        val borderColor by animateColorAsState(
            targetValue = when {
                hasError -> MaterialTheme.colorScheme.error
                bitmap != null -> Color.Transparent
                else -> MaterialTheme.colorScheme.surfaceContainerHighest
            },
            label = "Border color"
        )
        val backgroundColor by animateColorAsState(
            targetValue = if (hasError) MaterialTheme.colorScheme.errorContainer else Color.Transparent,
            label = "Background color"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(width = Two, color = borderColor, shape = CircleShape)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(color = borderColor),
                    onClick = onClick
                )
        ) {
            if (bitmap != null) {
                AsyncImage(
                    model = bitmap,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = borderColor,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(Forty)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AddCoinDetailsButtons(
    onSaveClicked: () -> Unit,
    onClearClicked: () -> Unit,
    isEditing: Boolean
) {
    PrimaryButton(
        text = stringResource(id = R.string.save),
        modifier = Modifier.padding(start = Twenty, top = Twenty, end = Twenty, bottom = Twelve),
        onClick = onSaveClicked
    )
    PrimaryOutlinedButton(
        text = stringResource(id = if (isEditing) R.string.cancel else R.string.clear),
        modifier = Modifier.padding(horizontal = Twenty),
        onClick = onClearClicked
    )
}