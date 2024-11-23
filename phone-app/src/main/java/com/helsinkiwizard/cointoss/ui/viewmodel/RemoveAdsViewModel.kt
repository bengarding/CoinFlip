package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoveAdsViewModel @Inject constructor(
    private val repo: Repository
) : AbstractViewModel() {

    fun onPurchaseCompleted() {
        viewModelScope.launch {
            repo.setAdsRemoved(true)
        }
    }
}