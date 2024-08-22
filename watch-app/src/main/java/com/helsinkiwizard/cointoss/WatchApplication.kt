package com.helsinkiwizard.cointoss

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.wearable.Wearable
import com.helsinkiwizard.core.CoreConstants.WEAR_CAPABILITY
import com.helsinkiwizard.core.utils.logging.CrashlyticsTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class WatchApplication : Application() {

    private val lifecycleObserver = AppLifecycleObserver()

    override fun onCreate() {
        super.onCreate()
        Wearable.getCapabilityClient(this).addLocalCapability(WEAR_CAPABILITY)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)
        initTimber()
    }

    private fun initTimber() = when {
        BuildConfig.DEBUG -> {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return super.createStackElementTag(element) + ":" + element.lineNumber
                }
            })
        }

        else -> {
            Timber.plant(CrashlyticsTree())
        }
    }
}
