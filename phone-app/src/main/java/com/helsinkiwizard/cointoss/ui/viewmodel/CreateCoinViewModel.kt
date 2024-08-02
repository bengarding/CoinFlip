package com.helsinkiwizard.cointoss.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityClient.FILTER_REACHABLE
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.helsinkiwizard.cointoss.data.Repository
import com.helsinkiwizard.cointoss.ui.model.CreateCoinModel
import com.helsinkiwizard.core.CoreConstants.BYTE_BUFFER_CAPACITY
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.coin.CoinSide
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CreateCoinViewModel @Inject constructor(
    private val repository: Repository
) : AbstractViewModel() {

    companion object {
        private const val START_ACTIVITY_PATH = "/start-activity"
        private const val IMAGE_PATH = "/image"
        private const val WEAR_CAPABILITY = "wear"
    }

    private val model = CreateCoinModel(
        selectedCoin = repository.getSelectedCustomCoin(),
        customCoins = repository.getCustomCoins(),
        showSendToWatchButton = repository.getShowSendToWatchButton
    )

    init {
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
        viewModelScope.launch {
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                messageClient.sendMessage(node.id, START_ACTIVITY_PATH, byteArrayOf()).await()

            } catch (cancellationException: CancellationException) {
                return@launch
            } catch (e: Exception) {
                onError(e)
                return@launch
            }

            try {
                val heads = uriToBitmap(coin.headsUri) ?: return@launch
                val tails = uriToBitmap(coin.tailsUri) ?: return@launch
                val headsByteArray = heads.toPrefixedByteArray()
                val tailsByteArray = tails.toPrefixedByteArray()
                val nameByteArray = coin.name.toPrefixedByteArray()
                val combinedData = headsByteArray + tailsByteArray + nameByteArray

                val channel = channelClient.openChannel(node.id, IMAGE_PATH).await()
                val outputStream = channelClient.getOutputStream(channel).await()

                outputStream.apply {
                    write(combinedData)
                    flush()
                    close()
                }

            } catch (cancellationException: CancellationException) {
                // do nothing
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /**
     * Converts the [Bitmap] to a byte array, compress it to a png image in a background thread.
     */
    private suspend fun Bitmap.toPrefixedByteArray(): ByteArray =
        withContext(Dispatchers.Default) {
            ByteArrayOutputStream().use { byteStream ->
                compress(Bitmap.CompressFormat.PNG, 100, byteStream)
                val bitmapData = byteStream.toByteArray()
                val size = bitmapData.size
                val sizeBytes = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY).putInt(size).array()
                sizeBytes + bitmapData
            }
        }

    private suspend fun String.toPrefixedByteArray(): ByteArray =
        withContext(Dispatchers.Default) {
            val stringData = toByteArray()
            val size = stringData.size
            val sizeBytes = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY).putInt(size).array()
            sizeBytes + stringData
        }
}

sealed interface CreateCoinContent : BaseType {
    data class LoadingComplete(val model: CreateCoinModel) : CreateCoinContent
}

sealed interface CreateCoinDialogs : BaseDialogType {
    data class MediaPicker(val coinSide: CoinSide) : CreateCoinDialogs
    data object MissingImages : CreateCoinDialogs
    data object SaveError : CreateCoinDialogs
    data class DeleteCoinBitmaps(val headsUri: Uri, val tailsUri: Uri) : CreateCoinDialogs
    data class DeleteCoinDialog(val coin: CustomCoinUiModel) : CreateCoinDialogs
    data object NoNodesFoundDialog : CreateCoinDialogs
    data class SelectNodesDialog(
        val coin: CustomCoinUiModel,
        val nodes: Set<Node>,
        val messageClient: MessageClient,
        val channelClient: ChannelClient,
        val uriToBitmap: (Uri) -> Bitmap?
    ) : CreateCoinDialogs
}
