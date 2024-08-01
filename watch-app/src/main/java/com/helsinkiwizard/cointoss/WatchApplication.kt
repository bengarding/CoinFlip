package com.helsinkiwizard.cointoss

import android.app.Application
import com.google.android.gms.wearable.Wearable
import com.helsinkiwizard.core.CoreConstants.WEAR_CAPABILITY
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WatchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Wearable.getCapabilityClient(this).addLocalCapability(WEAR_CAPABILITY)
    }
}
