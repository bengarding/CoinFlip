package com.helsinkiwizard.cointoss.datalayer

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.helsinkiwizard.cointoss.ui.MainActivity
import com.helsinkiwizard.cointoss.ui.ReceiveImageActivity
import com.helsinkiwizard.core.CoreConstants.NODE_ID
import com.helsinkiwizard.core.CoreConstants.PREPARE_FOR_COIN_TRANSFER

class DataLayerListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)
        when (messageEvent.path) {
            PREPARE_FOR_COIN_TRANSFER -> {
                val mainIntent = Intent(this, MainActivity::class.java)
                val receiveIntent = Intent(this, ReceiveImageActivity::class.java).apply {
                    putExtras(
                        Bundle().apply {
                            putString(NODE_ID, messageEvent.sourceNodeId)
                        }
                    )
                }
                TaskStackBuilder.create(this)
                    .addNextIntent(mainIntent)
                    .addNextIntent(receiveIntent)
                    .startActivities()
            }
        }
    }
}
