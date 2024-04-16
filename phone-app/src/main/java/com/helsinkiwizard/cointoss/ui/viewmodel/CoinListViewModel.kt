package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.coin.CoinType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
            repository.setCoinType(coinType)
            mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.CoinSet)
        }
    }
}

internal sealed interface CoinListContent: BaseType {
    data class LoadingComplete(val customCoinFlow: Flow<CustomCoinUiModel?>): CoinListContent
    data object CoinSet: CoinListContent
}
