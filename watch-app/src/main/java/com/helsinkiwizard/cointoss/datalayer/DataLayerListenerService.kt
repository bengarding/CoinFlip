package com.helsinkiwizard.cointoss.datalayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.helsinkiwizard.cointoss.ui.ReceiveImageActivity
import com.helsinkiwizard.core.CoreConstants.NODE_ID
import com.helsinkiwizard.core.CoreConstants.PREPARE_FOR_COIN_TRANSFER

class DataLayerListenerService : WearableListenerService() {

    @SuppressLint("WearRecents") // Ignore because the activity is very short-lived
    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            PREPARE_FOR_COIN_TRANSFER -> {
                val intent = Intent(this, ReceiveImageActivity::class.java).apply {
                    flags = FLAG_ACTIVITY_NEW_TASK
                    putExtras(
                        Bundle().apply {
                            putString(NODE_ID, messageEvent.sourceNodeId)
                        }
                    )
                }
                startActivity(intent)
            }
        }
    }
}
