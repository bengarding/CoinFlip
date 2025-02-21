package com.helsinkiwizard.cointoss.ui

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.data.ThemeMode
import com.helsinkiwizard.cointoss.ui.composable.PillButton
import com.helsinkiwizard.cointoss.ui.composable.PreviewSurface
import com.helsinkiwizard.cointoss.ui.composable.PrimaryOutlinedButton
import com.helsinkiwizard.cointoss.ui.composable.PrimarySlider
import com.helsinkiwizard.cointoss.ui.composable.PrimarySwitch
import com.helsinkiwizard.cointoss.ui.model.MutableInputWrapper
import com.helsinkiwizard.cointoss.ui.model.SettingsModel
import com.helsinkiwizard.cointoss.ui.viewmodel.SettingsContent
import com.helsinkiwizard.cointoss.ui.viewmodel.SettingsViewModel
import com.helsinkiwizard.cointoss.utils.AdManager
import com.helsinkiwizard.core.CoreConstants.SPEED_MAX
import com.helsinkiwizard.core.CoreConstants.SPEED_MIN
import com.helsinkiwizard.core.CoreConstants.SPEED_STEPS
import com.helsinkiwizard.core.theme.LocalActivity
import com.helsinkiwizard.core.theme.ThirtyTwo
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.theme.Twenty
import com.helsinkiwizard.core.viewmodel.UiState

@Composable
internal fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is UiState.ShowContent -> {
            when (val type = state.type as SettingsContent) {
                is SettingsContent.LoadingComplete -> Content(type.model, viewModel)
            }
        }

        else -> {}
    }
}

@Composable
private fun Content(
    model: SettingsModel,
    viewModel: SettingsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = ThirtyTwo)
    ) {
        ThemeButtons(
            themeModeWrapper = model.themeMode,
            materialYouWrapper = model.materialYou,
            themeModeOnclick = { themeMode -> viewModel.onThemeModeClicked(themeMode) },
            materialYouOnclick = { checked -> viewModel.onSwitchChecked(model.materialYou, checked) }
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = Twelve)
        )
        CoinSettings(
            speedWrapper = model.speed,
            showSendToWatchButtonWrapper = model.showSendToWatchButton,
            playSoundEffectWrapper = model.playSound,
            onSpeedChangeFinished = { viewModel.onSpeedValueChangeFinished() },
            onShowSendToWatchButtonChecked = { checked ->
                viewModel.onSwitchChecked(model.showSendToWatchButton, checked)
            },
            onPlaySoundEffectChecked = { checked ->
                viewModel.onSwitchChecked(model.playSound, checked)
            }
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = Twelve)
        )
        PrivacySettings()
    }
}

@Composable
private fun ThemeButtons(
    themeModeWrapper: MutableInputWrapper<ThemeMode>,
    materialYouWrapper: MutableInputWrapper<Boolean>,
    themeModeOnclick: (ThemeMode) -> Unit = {},
    materialYouOnclick: (Boolean) -> Unit = {}
) {
    Column {
        Title(R.string.theme)
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Twelve)
        ) {
            ThemeMode.entries.forEach { themeMode ->
                PillButton(
                    text = stringResource(id = themeMode.textRes),
                    iconVector = themeMode.iconVector,
                    iconRes = themeMode.iconRes,
                    selected = themeModeWrapper.value == themeMode,
                    onclick = { themeModeOnclick(themeMode) }
                )
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Spacer(modifier = Modifier.height(Twelve))
            PrimarySwitch(
                checked = materialYouWrapper.value,
                onCheckChanged = materialYouOnclick,
                label = stringResource(id = R.string.use_device_color_scheme),
                modifier = Modifier.padding(horizontal = Twenty)
            )
        }
    }
}

@Composable
private fun CoinSettings(
    speedWrapper: MutableInputWrapper<Float>,
    showSendToWatchButtonWrapper: MutableInputWrapper<Boolean>,
    playSoundEffectWrapper: MutableInputWrapper<Boolean>,
    onSpeedChangeFinished: () -> Unit,
    onShowSendToWatchButtonChecked: (Boolean) -> Unit,
    onPlaySoundEffectChecked: (Boolean) -> Unit,
) {
    Column {
        Title(textRes = R.string.coin)
        PrimarySlider(
            value = speedWrapper.value,
            minRange = SPEED_MIN,
            maxRange = SPEED_MAX,
            steps = SPEED_STEPS,
            title = stringResource(id = R.string.speed_seconds),
            onValueChange = { value -> speedWrapper.value = value },
            onValueChangeFinished = onSpeedChangeFinished,
            modifier = Modifier.padding(horizontal = Twelve)
        )
        PrimarySwitch(
            label = stringResource(id = R.string.show_send_to_watch_button),
            checked = showSendToWatchButtonWrapper.value,
            onCheckChanged = onShowSendToWatchButtonChecked,
            modifier = Modifier.padding(horizontal = Twenty)
        )
        PrimarySwitch(
            label =stringResource(id = R.string.play_sound),
            checked = playSoundEffectWrapper.value,
            onCheckChanged = onPlaySoundEffectChecked,
            modifier = Modifier.padding(horizontal = Twenty)
        )
    }
}

@Composable
private fun PrivacySettings() {
    val activity = LocalActivity.current
    if (AdManager.isGDPR(activity)) {
        Column {
            Title(R.string.privacy)
            PrimaryOutlinedButton(
                text = stringResource(id = R.string.update_privacy_consent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Twenty),
                onClick = {
                    AdManager.showConsentForm(activity)
                }
            )
        }
    }
}

@Composable
private fun Title(@StringRes textRes: Int) {
    Text(
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = Twelve, top = Twenty, end = Twelve, bottom = Twelve)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    val repository = Repository(LocalContext.current)
    val viewModel = SettingsViewModel(repository)
    val model = SettingsModel(ThemeMode.DARK, true, 3f, true, true)
    PreviewSurface {
        Content(model, viewModel)
    }
}
