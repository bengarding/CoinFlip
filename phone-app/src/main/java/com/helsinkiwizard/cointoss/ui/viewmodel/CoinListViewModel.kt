package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.core.coin.CoinType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CoinListViewModel @Inject constructor(
    private val repository: Repository
) : AbstractViewModel() {

    init {
        mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.LoadingComplete)
    }

    fun onCoinClick(coinType: CoinType) {
        viewModelScope.launch {
            repository.setCoinType(coinType)
            mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.CoinSet)
        }
    }
}

internal sealed interface CoinListContent: BaseType {
    data object LoadingComplete: CoinListContent
    data object CoinSet: CoinListContent
}
