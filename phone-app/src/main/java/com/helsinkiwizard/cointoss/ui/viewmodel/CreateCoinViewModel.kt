package com.helsinkiwizard.cointoss.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityClient.FILTER_REACHABLE
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.cointoss.utils.SendCustomCoinHelper
import com.helsinkiwizard.cointoss.utils.exception.GoogleAPIUnavailableException
import com.helsinkiwizard.cointoss.utils.exception.WearCapabilityUnavailableException
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.CoreConstants.WEAR_CAPABILITY
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.viewmodel.AbstractViewModel
import com.helsinkiwizard.core.viewmodel.BaseDialogType
import com.helsinkiwizard.core.viewmodel.BaseErrorType
import com.helsinkiwizard.core.viewmodel.BaseType
import com.helsinkiwizard.core.viewmodel.DialogState
import com.helsinkiwizard.core.viewmodel.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CreateCoinViewModel @Inject constructor(
    private val repository: Repository
) : AbstractViewModel() {

    private val model = CreateCoinModel(
        selectedCoin = repository.getSelectedCustomCoin(),
        customCoins = repository.getCustomCoins(),
        showSendToWatchButton = repository.getShowSendToWatchButton
    )

    init {
        showContent()
    }

    fun showContent() {
        mutableUiStateFlow.value = UiState.ShowContent(CreateCoinContent.LoadingComplete(model))
    }

    fun setBitmap(bitmap: Bitmap, coinSide: CoinSide) {
        if (coinSide == CoinSide.HEADS) {
            model.headsError = false
            model.headsBitmap = bitmap
        } else {
            model.tailsError = false
            model.tailsBitmap = bitmap
        }
    }

    fun onCoinSideClicked(coinSide: CoinSide) {
        mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.MediaPicker(coinSide))
    }

    fun saveCoin(storeBitmap: (Bitmap?) -> Uri?) {
        if (model.saveInProgress) return

        model.name.validate()
        model.headsError = model.headsBitmap == null
        model.tailsError = model.tailsBitmap == null

        if (model.headsError || model.tailsError || model.name.isError) {
            mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.MissingImages)
            model.saveInProgress = false
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            model.saveInProgress = true
            val headsUri = storeBitmap(model.headsBitmap)
            val tailsUri = storeBitmap(model.tailsBitmap)

            if (headsUri != null && tailsUri != null) {
                if (model.isEditing) {
                    repository.updateCustomCoin(headsUri, tailsUri, model.name.value, model.editingCoin.id)
                    mutableDialogStateFlow.value = DialogState.ShowContent(
                        CreateCoinDialogs.DeleteCoinBitmaps(model.editingCoin.headsUri, model.editingCoin.tailsUri)
                    )
                } else {
                    repository.storeCustomCoin(headsUri, tailsUri, model.name.value)
                }
                clear()
                mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.SaveSuccess)
            } else {
                mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.SaveError)
            }
            model.saveInProgress = false
        }
    }

    fun clear() {
        model.headsError = false
        model.tailsError = false
        model.headsBitmap = null
        model.tailsBitmap = null
        model.name.value = EMPTY_STRING
        model.editingCoin = CustomCoinUiModel.EMPTY
        model.isEditing = false
    }

    fun onNameChange(name: String) {
        with(model.name) {
            value = name
            if (isError) {
                validate()
            }
        }
    }

    fun onEditClicked(
        coin: CustomCoinUiModel,
        uriToBitmap: (Uri) -> Bitmap?
    ) {
        clear()
        model.headsBitmap = uriToBitmap(coin.headsUri)
        model.tailsBitmap = uriToBitmap(coin.tailsUri)
        model.name.value = coin.name
        model.editingCoin = coin
        model.isEditing = true
    }

    fun onDeleteClicked(coin: CustomCoinUiModel) {
        mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.DeleteCoinDialog(coin))
    }

    fun deleteCoin(coin: CustomCoinUiModel) {
        viewModelScope.launch {
            val headsUri = coin.headsUri
            val tailsUri = coin.tailsUri
            val isSelectedCoin = coin.id == model.selectedCoin.first()?.id

            repository.deleteCustomCoin(coin.id, isSelectedCoin)
            mutableDialogStateFlow.value = DialogState.ShowContent(
                CreateCoinDialogs.DeleteCoinBitmaps(headsUri, tailsUri)
            )
        }
    }

    fun setSelectedCoin(coin: CustomCoinUiModel) {
        viewModelScope.launch {
            repository.selectCustomCoin(coin.id)
        }
    }

    fun sendCoinToWatch(
        coin: CustomCoinUiModel,
        messageClient: MessageClient,
        capabilityClient: CapabilityClient,
        channelClient: ChannelClient,
        uriToBitmap: (Uri) -> Bitmap?
    ) {
        viewModelScope.safeLaunch(
            typeOfError = CreateCoinError.SendToWatchError(
                messageRes = R.string.error_sending_coin_api_exception,
                retry = { sendCoinToWatch(coin, messageClient, capabilityClient, channelClient, uriToBitmap) }
            )
        ) {
            val nodes = capabilityClient
                .getCapability(WEAR_CAPABILITY, FILTER_REACHABLE)
                .await()
                .nodes

            when (nodes.size) {
                0 -> mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.NoNodesFoundDialog)
                1 -> sendCoinToNode(nodes.first(), coin, messageClient, channelClient, uriToBitmap)
                else -> mutableDialogStateFlow.value = DialogState.ShowContent(
                    CreateCoinDialogs.SelectNodesDialog(coin, nodes, messageClient, channelClient, uriToBitmap)
                )
            }
        }
    }

    fun sendCoinToNode(
        node: Node,
        coin: CustomCoinUiModel,
        messageClient: MessageClient,
        channelClient: ChannelClient,
        uriToBitmap: (Uri) -> Bitmap?
    ) {
        viewModelScope.safeLaunch(context = Dispatchers.IO) {
            val onFinished: (SendCustomCoinHelper.FinishedResult) -> Unit = { result ->
                if (result == SendCustomCoinHelper.FinishedResult.SUCCESS) {
                    showContent()
                    mutableDialogStateFlow.value = DialogState.ShowContent(CreateCoinDialogs.SendToWatchSuccess)
                } else {
                    val exception = (result as SendCustomCoinHelper.FinishedResult.FAILURE).exception
                    val messageRes = if (exception is GoogleAPIUnavailableException
                        || exception is WearCapabilityUnavailableException
                    ) {
                        R.string.error_sending_coin_api_exception
                    } else {
                        R.string.error_sending_coin_to_watch
                    }

                    onError(
                        e = exception,
                        errorType = CreateCoinError.SendToWatchError(
                            messageRes = messageRes,
                            retry = { sendCoinToNode(node, coin, messageClient, channelClient, uriToBitmap) }
                        )
                    )
                }
            }

            val helper = SendCustomCoinHelper(
                node, coin, messageClient, channelClient, viewModelScope, uriToBitmap, onFinished
            )
            helper.sendCoin()
        }
    }
}

sealed interface CreateCoinContent : BaseType {
    data class LoadingComplete(val model: CreateCoinModel) : CreateCoinContent
}

sealed interface CreateCoinDialogs : BaseDialogType {
    data class MediaPicker(val coinSide: CoinSide) : CreateCoinDialogs
    data object MissingImages : CreateCoinDialogs
    data object SaveSuccess : CreateCoinDialogs
    data object SaveError : CreateCoinDialogs
    data class DeleteCoinBitmaps(val headsUri: Uri, val tailsUri: Uri) : CreateCoinDialogs
    data class DeleteCoinDialog(val coin: CustomCoinUiModel) : CreateCoinDialogs
    data object SendToWatchSuccess : CreateCoinDialogs
    data object NoNodesFoundDialog : CreateCoinDialogs
    data class SelectNodesDialog(
        val coin: CustomCoinUiModel,
        val nodes: Set<Node>,
        val messageClient: MessageClient,
        val channelClient: ChannelClient,
        val uriToBitmap: (Uri) -> Bitmap?
    ) : CreateCoinDialogs
}

sealed interface CreateCoinError : BaseErrorType {
    data class SendToWatchError(val messageRes: Int, val retry: () -> Unit) : CreateCoinError
}
