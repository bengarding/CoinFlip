package com.helsinkiwizard.cointoss.ui.model

import android.net.Uri

class CustomCoinUiModel(
    val id: Int,
    val headsUri: Uri,
    val tailsUri: Uri,
    val name: String,
    val selected: Boolean
)