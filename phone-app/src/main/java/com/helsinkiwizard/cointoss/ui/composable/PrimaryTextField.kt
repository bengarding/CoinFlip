package com.helsinkiwizard.cointoss.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.theme.Alpha40
import com.helsinkiwizard.core.theme.Six
import com.helsinkiwizard.core.theme.Twelve

@Composable
fun PrimaryTextFieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    focusedLabelColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    cursorColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    selectionColors = TextSelectionColors(
        backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = Alpha40),
        handleColor = MaterialTheme.colorScheme.surfaceContainerHighest
    )
)

@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorText: String = EMPTY_STRING,
    singleLine: Boolean = true,
    markOptional: Boolean = false,
) {
    Column(
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            colors = PrimaryTextFieldColors(),
            label = {
                val annotatedLabel = if (markOptional) label.appendOptional() else AnnotatedString(label)
                Text(text = annotatedLabel)
            },
            isError = isError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            singleLine = singleLine,
            trailingIcon = {
                if (isError) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        ErrorText(
            isError = isError,
            errorText = errorText
        )
    }
}

@Composable
private fun ErrorText(
    isError: Boolean,
    errorText: String
) {
    AnimatedVisibility(
        visible = isError && errorText.isNotEmpty(),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Text(
            text = errorText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(start = Six, top = Six)
                .semantics { liveRegion = LiveRegionMode.Polite }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimaryTextFieldPreview() {
    PreviewSurface {
        Column(
            verticalArrangement = Arrangement.spacedBy(Twelve),
            modifier = Modifier.padding(all = Twelve)
        ) {
            PrimaryTextField(
                value = "Name entered",
                onValueChange = {},
                label = "Coin name"
            )

            PrimaryTextField(
                value = "Name entered error",
                onValueChange = {},
                label = "Coin name",
                isError = true,
                errorText = "Enter a valid name"
            )

            PrimaryTextField(
                value = "Name entered optional",
                onValueChange = {},
                label = "Coin name",
                markOptional = true
            )
        }
    }
}
