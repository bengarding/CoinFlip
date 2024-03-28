package com.helsinkiwizard.cointoss.ui.model

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class CreateCoinModel {
    var headsBitmap by mutableStateOf<Bitmap?>(null)
    var tailsBitmap by mutableStateOf<Bitmap?>(null)
    var headsError by mutableStateOf(false)
    var tailsError by mutableStateOf(false)
}
