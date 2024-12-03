package com.helsinkiwizard.cointoss.coin

import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    repository: Repository
) : AbstractViewModel() {

    val customCoinFlow = repository.getCustomCoin

    fun launchPhoneApp() {

    }
}

