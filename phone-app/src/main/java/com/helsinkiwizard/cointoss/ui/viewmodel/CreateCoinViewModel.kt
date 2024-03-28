package com.helsinkiwizard.cointoss.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.core.coin.CoinSide
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateCoinViewModel @Inject constructor(
    private val repository: Repository
) : AbstractViewModel() {

    private val model = CreateCoinModel()

    init {
        mutableUiStateFlow.value = UiState.ShowContent(CreateCoinContent.LoadingComplete(model))
    }

    fun setBitmap(bitmap: Bitmap, coinSide: CoinSide) {
        if (coinSide == CoinSide.HEADS) {
            model.headsError = false
            model.headsBitmap = bitmap
        } else {
            model.tailsError = false
            model.tailsBitmap = bitmap
        }
    }

    fun onCoinSideClicked(coinSide: CoinSide) {
        mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.MediaPicker(coinSide))
    }

    fun saveCoin(storeBitmap: (Bitmap?) -> Uri?) {
        model.headsError = model.headsBitmap == null
        model.tailsError = model.tailsBitmap == null

        if (model.headsError || model.tailsError) {
            mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.MissingImages)
            return
        }

        val headsUri = storeBitmap(model.headsBitmap)
        val tailsUri = storeBitmap(model.tailsBitmap)

        if (headsUri != null && tailsUri != null) {

        } else {
            mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.SaveError)
        }
    }

    fun clear() {
        model.headsError = false
        model.tailsError = false
        model.headsBitmap = null
        model.tailsBitmap = null
    }
}

sealed interface CreateCoinContent : BaseType {
    data class LoadingComplete(val model: CreateCoinModel) : CreateCoinContent
}

sealed interface CreateCoinDialogs : BaseDialogType {
    data class MediaPicker(val coinSide: CoinSide) : CreateCoinDialogs
    data object MissingImages : CreateCoinDialogs
    data object SaveError : CreateCoinDialogs
}
