package com.helsinkiwizard.cointoss.ui.model

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CreateCoinModel(
    val customCoins: Flow<List<CustomCoinUiModel>> = flowOf()
) {
    var headsBitmap by mutableStateOf<Bitmap?>(null)
    var tailsBitmap by mutableStateOf<Bitmap?>(null)
    var headsError by mutableStateOf(false)
    var tailsError by mutableStateOf(false)
    val name = MutableInputWrapper(EMPTY_STRING)
}
