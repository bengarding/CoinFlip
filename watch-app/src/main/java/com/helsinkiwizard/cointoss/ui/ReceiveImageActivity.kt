package com.helsinkiwizard.cointoss.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.compose.layout.fillMaxRectangle
import com.helsinkiwizard.cointoss.R
import com.helsinkiwizard.cointoss.Repository
import com.helsinkiwizard.cointoss.tile.CoinTileService
import com.helsinkiwizard.core.CoreConstants.BYTE_BUFFER_CAPACITY
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.CoreConstants.IMAGE_PATH
import com.helsinkiwizard.core.CoreConstants.NODE_ID
import com.helsinkiwizard.core.CoreConstants.READY_FOR_COIN_TRANSFER
import com.helsinkiwizard.core.coin.CoinType
import com.helsinkiwizard.core.theme.CoinTossTheme
import com.helsinkiwizard.core.theme.Forty
import com.helsinkiwizard.core.theme.Twelve
import com.helsinkiwizard.core.utils.deleteBitmap
import com.helsinkiwizard.core.utils.storeBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.nio.ByteBuffer
import javax.inject.Inject

@AndroidEntryPoint
class ReceiveImageActivity : ComponentActivity() {

    @Inject
    lateinit var repo: Repository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val channelClient by lazy { Wearable.getChannelClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }

    private val channelCallback = object : ChannelClient.ChannelCallback() {
        override fun onChannelOpened(channel: ChannelClient.Channel) {
            if (channel.path == IMAGE_PATH) {
                receiveImage(channel)
            }
        }

        override fun onInputClosed(channel: ChannelClient.Channel, p1: Int, p2: Int) {
            super.onInputClosed(channel, p1, p2)
            channelClient.close(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(FLAG_KEEP_SCREEN_ON)
        channelClient.registerChannelCallback(channelCallback)

        val nodeId = intent.getStringExtra(NODE_ID) ?: EMPTY_STRING
        runBlocking {
            messageClient.sendMessage(nodeId, READY_FOR_COIN_TRANSFER, byteArrayOf())
        }
        super.onCreate(savedInstanceState)
        setContent {
            CoinTossTheme {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxRectangle()
                ) {
                    Text(
                        text = stringResource(id = R.string.receiving_custom_coin),
                        modifier = Modifier.padding(bottom = Twelve)
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(Forty)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        channelClient.unregisterChannelCallback(channelCallback)
        super.onDestroy()
        scope.cancel()
    }

    @SuppressLint("WearRecents") // ignore because MainActivity is set to singleTop in the manifest
    private fun receiveImage(channel: ChannelClient.Channel) {
        scope.launch {
            val inputStream = channelClient.getInputStream(channel).await()
            val byteArray = inputStream.readBytes()
            val (heads, tails, name) = byteArray.toBitmapAndString()
            inputStream.close()

            updateImage(heads, tails, name)
            val intent = Intent(this@ReceiveImageActivity, MainActivity::class.java).apply {
                flags = FLAG_ACTIVITY_CLEAR_TOP or FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    private suspend fun updateImage(heads: Bitmap, tails: Bitmap, name: String) {
        val headsUri = storeBitmap(applicationContext, heads)
        val tailsUri = storeBitmap(applicationContext, tails)

        if (headsUri != null && tailsUri != null) {
            val oldCoin = repo.getCustomCoin.firstOrNull()
            repo.setCustomCoin(headsUri, tailsUri, name)
            repo.setCoinType(CoinType.CUSTOM)
            TileService.getUpdater(applicationContext).requestUpdate(CoinTileService::class.java)

            if (oldCoin != null) {
                deleteBitmap(applicationContext, oldCoin.headsUri)
                deleteBitmap(applicationContext, oldCoin.tailsUri)
            }
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
