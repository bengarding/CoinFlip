package com.helsinkiwizard.cointoss.ui.model

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.helsinkiwizard.cointoss.ui.composable.xssValidator
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CreateCoinModel(
    val selectedCoin: Flow<CustomCoinUiModel?> = flowOf(),
    val customCoins: Flow<List<CustomCoinUiModel>> = flowOf()
) {
    var headsBitmap by mutableStateOf<Bitmap?>(null)
    var tailsBitmap by mutableStateOf<Bitmap?>(null)
    var headsError by mutableStateOf(false)
    var tailsError by mutableStateOf(false)
    val name = MutableInputWrapper(EMPTY_STRING).apply {
        validator = xssValidator()
    }

    var editingCoin: CustomCoinUiModel = CustomCoinUiModel.EMPTY
    var isEditing by mutableStateOf(false)
}
