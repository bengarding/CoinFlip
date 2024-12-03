package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import com.helsinkiwizard.core.viewmodel.BaseDialogType
import com.helsinkiwizard.core.viewmodel.BaseType
import com.helsinkiwizard.core.viewmodel.DialogState
import com.helsinkiwizard.core.viewmodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CoinListViewModel @Inject constructor(
    private val repository: Repository
) : AbstractViewModel() {

    private val customCoin = repository.getSelectedCustomCoin()

    init {
        mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.LoadingComplete(customCoin))
    }

    fun onCoinClick(coinType: CoinType) {
        viewModelScope.launch {
            // Show in-app review only if the user has previously changed coins
            val showInAppReview = repository.getCoinType.first() != CoinType.BITCOIN
            repository.setCoinType(coinType)

            if (showInAppReview) {
                mutableDialogStateFlow.value = DialogState.ShowContent(CoinListDialogs.InAppReview(
                    onComplete = { mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.CoinSet) }
                ))
            } else {
                mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.CoinSet)
            }
        }
    }
}

internal sealed interface CoinListContent : BaseType {
    data class LoadingComplete(val customCoinFlow: Flow<CustomCoinUiModel?>) : CoinListContent
    data object CoinSet : CoinListContent
}

internal sealed interface CoinListDialogs : BaseDialogType {
    data class InAppReview(val onComplete: () -> Unit) : CoinListDialogs
}
