package com.helsinkiwizard.core.ui.model

import android.net.Uri
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.CoreConstants.VALUE_UNDEFINED

class CustomCoinUiModel(
    val id: Int = VALUE_UNDEFINED,
    val headsUri: Uri,
    val tailsUri: Uri,
    val name: String
) {
    companion object {
        val EMPTY = CustomCoinUiModel(VALUE_UNDEFINED, Uri.EMPTY, Uri.EMPTY, EMPTY_STRING)
    }

    fun isEmpty() = headsUri == Uri.EMPTY
}
