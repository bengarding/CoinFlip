package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import com.helsinkiwizard.core.viewmodel.BaseType
import com.helsinkiwizard.core.viewmodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val repository: Repository
) : AbstractViewModel() {

    val coinTypeFlow = repository.getCoinType
    val speedFlow = repository.getSpeed
    val customCoinFlow = repository.getSelectedCustomCoin()
    val adsRemoved = repository.getAdsRemoved
    val playSound = repository.getPlaySound

    init {
        viewModelScope.launch {
            val initialCoinType = repository.getCoinType.filterNotNull().first()
            val initialSpeed = repository.getSpeed.filterNotNull().first()
            val playSoundEffect = repository.getPlaySound.filterNotNull().first()
            mutableUiStateFlow.value = UiState.ShowContent(
                HomeScreenContent.LoadingComplete(initialCoinType, initialSpeed, playSoundEffect)
            )
        }
    }
}

internal sealed interface HomeScreenContent : BaseType {
    data class LoadingComplete(
        val initialCoinType: CoinType,
        val initialSpeed: Float,
        val playSound: Boolean,
    ) : HomeScreenContent
}
