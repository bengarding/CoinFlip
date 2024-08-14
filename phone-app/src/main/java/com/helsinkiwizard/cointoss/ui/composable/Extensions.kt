package com.helsinkiwizard.cointoss.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.core.CoreConstants.SPACE_STRING

@Composable
fun String.appendOptional() = buildAnnotatedString {
    append(this@appendOptional)
    append(SPACE_STRING)
    append(stringResource(id = R.string.append_optional))
}
