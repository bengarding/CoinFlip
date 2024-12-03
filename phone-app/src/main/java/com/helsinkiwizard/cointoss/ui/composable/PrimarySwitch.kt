package com.helsinkiwizard.cointoss.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.core.theme.Eight

@Composable
fun PrimarySwitch(
    label: String,
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    enabled: Boolean = true,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    textComposable: @Composable RowScope.() -> Unit = {
        Text(
            text = label,
            style = textStyle,
            modifier = Modifier.weight(1f)
        )
    }
) {
    val combinedModifier = Modifier
        .fillMaxWidth()
        .toggleable(
            value = checked,
            onValueChange = onCheckChanged
        )
        .minimumInteractiveComponentSize()
        .semantics(mergeDescendants = true) {}
        // the passed in modifier values should be added to or override the above values
        .then(modifier)

    Row(
        modifier = combinedModifier,
        verticalAlignment = verticalAlignment
    ) {
        textComposable()
        Switch(
            modifier = Modifier
                .padding(start = Eight)
                .clearAndSetSemantics { },
            checked = checked,
            onCheckedChange = onCheckChanged,
            enabled = enabled
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrimarySwitchPreview() {
    PreviewSurface {
        PrimarySwitch(
            label = stringResource(id = R.string.show_send_to_watch_button),
            checked = true,
            onCheckChanged = {}
        )
    }
}
