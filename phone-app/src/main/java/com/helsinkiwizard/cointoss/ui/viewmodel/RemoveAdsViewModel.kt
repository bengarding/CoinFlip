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
) : AbstractViewModel() {

    init {
        showDialog()
    }

    fun showDialog() {
        mutableUiStateFlow.value = UiState.ShowContent(RemoveAdsContent.ShowDialog)
    }

    fun onPurchaseCompleted() {
        viewModelScope.launch {
            repo.setAdsRemoved(true)
            mutableUiStateFlow.value = UiState.ShowContent(RemoveAdsContent.PurchaseComplete)
        }
    }

    fun onPurchaseError() {
        mutableUiStateFlow.value = UiState.Error()
    }

    fun onPurchaseCancelled() {
        mutableUiStateFlow.value = UiState.ShowContent(RemoveAdsContent.PurchaseCancelled)
    }
}

sealed interface RemoveAdsContent : BaseType {
    data object ShowDialog : RemoveAdsContent
    data object PurchaseComplete : RemoveAdsContent
    data object PurchaseCancelled : RemoveAdsContent
}
