package com.helsinkiwizard.cointoss

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.wearable.Wearable
import com.helsinkiwizard.core.CoreConstants.WEAR_CAPABILITY

class WatchApplication : Application() {

    private val lifecycleObserver = AppLifecycleObserver()

    override fun onCreate() {
        super.onCreate()
        Wearable.getCapabilityClient(this).addLocalCapability(WEAR_CAPABILITY)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
    }
}
