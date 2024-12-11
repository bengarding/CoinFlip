package com.helsinkiwizard.cointoss.coin

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.utils.isAppInstalledOnPhone
import com.helsinkiwizard.cointoss.utils.isConnectedToAnyNode
import com.helsinkiwizard.cointoss.utils.launchDeepLinkOnPhone
import com.helsinkiwizard.core.CoreConstants.CREATE_COIN_DEEPLINK
import com.helsinkiwizard.core.CoreConstants.PLAY_STORE_DEEPLINK
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import com.helsinkiwizard.core.viewmodel.BaseDialogType
import com.helsinkiwizard.core.viewmodel.BaseType
import com.helsinkiwizard.core.viewmodel.DialogState
import com.helsinkiwizard.core.viewmodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    repository: Repository
) : AbstractViewModel(defaultState = UiState.ShowContent(CoinListContent.ShowCoinList)) {

    val customCoinFlow = repository.getCustomCoin

    fun onBlankCustomCoinClicked(
        capabilityClient: CapabilityClient,
        nodeClient: NodeClient,
        remoteActivityHelper: RemoteActivityHelper
    ) {
        viewModelScope.launch {
            mutableUiStateFlow.value = UiState.Loading

            val appInstalledOnPhone = isAppInstalledOnPhone(capabilityClient)
            val connectedToAnyNode = isConnectedToAnyNode(nodeClient)

            if (connectedToAnyNode.not()) {
                mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.ShowCoinList)
                mutableDialogStateFlow.value = DialogState.ShowContent(CoinListDialogs.DownloadMobileApp)
                return@launch
            }

            val deepLink = if (appInstalledOnPhone) CREATE_COIN_DEEPLINK else PLAY_STORE_DEEPLINK
            val messageRes = if (appInstalledOnPhone) R.string.create_coin_on_phone else R.string.download_mobile_app

            val deepLinkLaunched = launchDeepLinkOnPhone(
                remoteActivityHelper = remoteActivityHelper,
                deepLink = deepLink
            )

            mutableUiStateFlow.value = UiState.ShowContent(CoinListContent.ShowCoinList)
            mutableDialogStateFlow.value = if (deepLinkLaunched) {
                DialogState.ShowContent(CoinListDialogs.OpenOnPhone(messageRes = messageRes))
            } else {
                DialogState.ShowContent(CoinListDialogs.DownloadMobileApp)
            }
        }
    }
}

internal sealed interface CoinListContent : BaseType {
    data object ShowCoinList : CoinListContent
}

internal sealed interface CoinListDialogs : BaseDialogType {
    data class OpenOnPhone(@StringRes val messageRes: Int) : CoinListDialogs
    data object DownloadMobileApp : CoinListDialogs
}
