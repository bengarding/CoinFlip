package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import com.helsinkiwizard.core.viewmodel.BaseType
import com.helsinkiwizard.core.viewmodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemoveAdsViewModel @Inject constructor(
    private val repo: Repository
) : AbstractViewModel(defaultState = UiState.ShowContent(RemoveAdsContent.ShowDialog)) {
    fun onPurchaseCompleted() {
        viewModelScope.launch {
            repo.setAdsRemoved(true)
            mutableUiStateFlow.value = UiState.ShowContent(RemoveAdsContent.PurchaseComplete)
        }
    }
}

sealed interface RemoveAdsContent : BaseType {
    data object ShowDialog : RemoveAdsContent
    data object PurchaseComplete : RemoveAdsContent
}
