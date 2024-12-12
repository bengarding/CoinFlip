package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CoinTossViewModel @Inject constructor(
    repo: Repository
) : AbstractViewModel() {

    val coinTypeFlow = repo.getCoinType.stateIn(
       scope =  viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = runBlocking { repo.getCoinType.first() }
    )

    val customCoinFlow = repo.getCustomCoin
    var startFlipping by mutableStateOf(false)
}
