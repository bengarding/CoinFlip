package com.helsinkiwizard.cointoss.utils

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.scale
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.helsinkiwizard.core.CoreConstants.BYTE_BUFFER_CAPACITY
import com.helsinkiwizard.core.CoreConstants.IMAGE_PATH
import com.helsinkiwizard.core.CoreConstants.PREPARE_FOR_COIN_TRANSFER
import com.helsinkiwizard.core.CoreConstants.READY_FOR_COIN_TRANSFER
import com.helsinkiwizard.core.CoreConstants.TRANSFER_COMPLETE
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

internal class SendCustomCoinHelper(
    private val node: Node,
    private val coin: CustomCoinUiModel,
    private val messageClient: MessageClient,
    private val channelClient: ChannelClient,
    private val scope: CoroutineScope,
    private val uriToBitmap: (Uri) -> Bitmap?,
    private val onFinished: (FinishedResult) -> Unit
) {

    companion object {
        private const val SCALED_BITMAP_SIZE = 500
    }

    private val messageListener = MessageClient.OnMessageReceivedListener { message ->
        when (message.path) {
            READY_FOR_COIN_TRANSFER -> openChannelAndSendCoin()
            TRANSFER_COMPLETE -> completeTransfer()
        }
    }

    suspend fun sendCoin() {
        val isGooglePlayServicesAvailable = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(messageClient.applicationContext)

        if (isGooglePlayServicesAvailable != ConnectionResult.SUCCESS) {
            onFinished(FinishedResult.FAILURE(Exception("Google API is not available")))
            return
        }

        try {
            messageClient.addListener(messageListener)
            messageClient.sendMessage(node.id, PREPARE_FOR_COIN_TRANSFER, byteArrayOf()).await()
        } catch (e: Exception) {
            onFinished(FinishedResult.FAILURE(e))
        }
    }

    private fun openChannelAndSendCoin() {
        scope.launch(Dispatchers.IO) {
            try {
                val headsByteArray = coin.headsUri.toBitmapByteArray(uriToBitmap)
                val tailsByteArray = coin.tailsUri.toBitmapByteArray(uriToBitmap)
                val nameByteArray = coin.name.toPrefixedByteArray()

                if (headsByteArray == null || tailsByteArray == null) {
                    onFinished(FinishedResult.FAILURE(Exception("Converting uri to bitmap failed")))
                    return@launch
                }

                val combinedData = headsByteArray + tailsByteArray + nameByteArray

                val channel = channelClient.openChannel(node.id, IMAGE_PATH).await()
                val outputStream = channelClient.getOutputStream(channel).await()

                outputStream.apply {
                    write(combinedData)
                    flush()
                    close()
                }
            } catch (e: Exception) {
                onFinished(FinishedResult.FAILURE(e))
            }
        }
    }

    private fun Uri.toBitmapByteArray(uriToBitmap: (Uri) -> Bitmap?): ByteArray? {
        val bitmap = uriToBitmap(this) ?: return null

        val scaledBitmap = bitmap.scale(SCALED_BITMAP_SIZE, SCALED_BITMAP_SIZE)

        ByteArrayOutputStream().use { byteStream ->
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
            val bitmapData = byteStream.toByteArray()
            val size = bitmapData.size
            val sizeBytes = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY).putInt(size).array()
            return sizeBytes + bitmapData
        }
    }

    private fun String.toPrefixedByteArray(): ByteArray {
        val stringData = toByteArray()
        val size = stringData.size
        val sizeBytes = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY).putInt(size).array()
        return sizeBytes + stringData
    }

    private fun completeTransfer() {
        messageClient.removeListener(messageListener)
        onFinished(FinishedResult.SUCCESS)
    }

    sealed class FinishedResult {
        data object SUCCESS : FinishedResult()
        data class FAILURE(val exception: Exception) : FinishedResult()
    }
}
