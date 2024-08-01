package com.helsinkiwizard.cointoss

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver : DefaultLifecycleObserver {

    var isAppInForeground = false
        private set

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        isAppInForeground = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        isAppInForeground = false
    }
}
