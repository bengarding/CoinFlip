package com.helsinkiwizard.cointoss

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.helsinkiwizard.cointoss.Constants.REVENUE_CAT_KEY
import com.helsinkiwizard.core.utils.logging.CrashlyticsTree
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltAndroidApp
class PhoneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(PurchasesConfiguration.Builder(this, REVENUE_CAT_KEY).build())
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@PhoneApplication)
        }
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
