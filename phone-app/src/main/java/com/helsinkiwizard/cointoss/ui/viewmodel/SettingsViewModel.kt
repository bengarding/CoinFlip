package com.helsinkiwizard.cointoss.ui.viewmodel

import androidx.lifecycle.viewModelScope
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.data.ThemeMode
import com.helsinkiwizard.cointoss.ui.model.MutableInputWrapper
import com.helsinkiwizard.cointoss.ui.model.SettingsModel
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import com.helsinkiwizard.core.viewmodel.BaseType
import com.helsinkiwizard.core.viewmodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val repository: Repository
) : AbstractViewModel() {

    private lateinit var model: SettingsModel

    init {
        viewModelScope.launch {
            model = SettingsModel(
                themeMode = repository.getThemeMode.filterNotNull().first(),
                materialYou = repository.getMaterialYou.filterNotNull().first(),
                speed = repository.getSpeed.filterNotNull().first(),
                showSendToWatchButton = repository.getShowSendToWatchButton.filterNotNull().first()
            )
            mutableUiStateFlow.value = UiState.ShowContent(SettingsContent.LoadingComplete(model))
        }
    }

    fun onThemeModeClicked(themeMode: ThemeMode) {
        model.themeMode.value = themeMode
        viewModelScope.launch {
            repository.setTheme(themeMode)
        }
    }

    fun onSpeedValueChangeFinished() {
        viewModelScope.launch {
            repository.setSpeed(model.speed.value)
        }
    }

    fun onSwitchChecked(wrapper: MutableInputWrapper<Boolean>, checked: Boolean) {
        wrapper.value = checked
        viewModelScope.launch {
            when (wrapper) {
                model.showSendToWatchButton -> repository.setShowSendToWatchButton(checked)
                model.materialYou -> repository.setMaterialYou(checked)
            }
        }
    }
}

internal sealed interface SettingsContent : BaseType {
    data class LoadingComplete(val model: SettingsModel) : SettingsContent
}
