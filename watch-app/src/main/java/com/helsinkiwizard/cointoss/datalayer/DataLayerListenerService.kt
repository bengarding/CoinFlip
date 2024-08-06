package com.helsinkiwizard.cointoss.datalayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.WatchApplication
import com.helsinkiwizard.cointoss.ui.MainActivity
import com.helsinkiwizard.core.CoreConstants.BYTE_BUFFER_CAPACITY
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
import java.nio.ByteBuffer
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

    // Suppressed because MainActivity is set to "singleTop" in manifest
    @SuppressLint("WearRecents")
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            START_ACTIVITY_PATH -> {
                val isAppInForeground = (application as WatchApplication).lifecycleObserver.isAppInForeground
                if (isAppInForeground.not()) {
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }
    }

    private fun receiveImage(channel: ChannelClient.Channel) {
        CoroutineScope(Dispatchers.IO).launch {
            val inputStream = channelClient.getInputStream(channel).await()
            val byteArray = inputStream.readBytes()
            val (heads, tails, name) = byteArray.toBitmapAndString()
            inputStream.close()

            withContext(Dispatchers.Main) {
                updateImage(heads, tails, name)
            }
        }
    }

    private suspend fun updateImage(heads: Bitmap, tails: Bitmap, name: String) {
        val headsUri = storeBitmap(applicationContext, heads, HEADS_IMAGE_NAME)
        val tailsUri = storeBitmap(applicationContext, tails, TAILS_IMAGE_NAME)

        if (headsUri != null && tailsUri != null) {
            repo.setCustomCoinName(name)
            repo.setCoinType(CoinType.CUSTOM)
        }
    }

    private fun ByteArray.toBitmapAndString(): Triple<Bitmap, Bitmap, String> {
        var offset = 0

        fun readInt(): Int {
            val intBytes = this.copyOfRange(offset, offset + BYTE_BUFFER_CAPACITY)
            offset += BYTE_BUFFER_CAPACITY
            return ByteBuffer.wrap(intBytes).int
        }

        fun readByteArray(size: Int): ByteArray {
            val byteArray = this.copyOfRange(offset, offset + size)
            offset += size
            return byteArray
        }

        val headsSize = readInt()
        val headsData = readByteArray(headsSize)
        val headsBitmap = BitmapFactory.decodeByteArray(headsData, 0, headsSize)

        val tailsSize = readInt()
        val tailsData = readByteArray(tailsSize)
        val tailsBitmap = BitmapFactory.decodeByteArray(tailsData, 0, tailsSize)

        val nameSize = readInt()
        val nameData = readByteArray(nameSize)
        val name = String(nameData)

        return Triple(headsBitmap, tailsBitmap, name)
    }
}
