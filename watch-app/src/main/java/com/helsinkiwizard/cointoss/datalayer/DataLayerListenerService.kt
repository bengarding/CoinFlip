package com.helsinkiwizard.cointoss.datalayer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.ui.MainActivity
import com.helsinkiwizard.core.CoreConstants.START_ACTIVITY_PATH
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.utils.storeBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DataLayerListenerService : WearableListenerService() {

    companion object {
        const val IMAGE_PATH = "/image"
        const val HEADS_IMAGE_NAME = 0
        const val TAILS_IMAGE_NAME = 1
    }

    @Inject
    lateinit var repo: Repository

    private val channelClient by lazy { Wearable.getChannelClient(this) }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val channelCallback = object : ChannelClient.ChannelCallback() {
        override fun onChannelOpened(channel: ChannelClient.Channel) {
            if (channel.path == IMAGE_PATH) {
                receiveImage(channel)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        channelClient.registerChannelCallback(channelCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        channelClient.unregisterChannelCallback(channelCallback)
        scope.cancel()
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            START_ACTIVITY_PATH -> {
                startActivity(
                    Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }

    private fun receiveImage(channel: ChannelClient.Channel) {
        CoroutineScope(Dispatchers.IO).launch {
            val inputStream = channelClient.getInputStream(channel).await()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            withContext(Dispatchers.Main) {
                updateImage(bitmap)
            }
        }
    }

    private suspend fun updateImage(bitmap: Bitmap) {
        val headsUri = storeBitmap(applicationContext, bitmap, HEADS_IMAGE_NAME)
        val tailsUri = storeBitmap(applicationContext, bitmap, TAILS_IMAGE_NAME)

        if (headsUri != null && tailsUri != null) {
            repo.setCustomCoin(headsUri, tailsUri)
            repo.setCoinType(CoinType.CUSTOM)
        }
    }
}
