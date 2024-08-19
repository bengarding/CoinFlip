package com.helsinkiwizard.core.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

object Logger {
    fun e(tag: String?, message: String?, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        FirebaseCrashlytics.getInstance().log("ERROR:/$tag: $message")
        if (throwable != null) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
        FirebaseCrashlytics.getInstance().log("WARN/$tag: $message")
        throwable?.let {
            FirebaseCrashlytics.getInstance().recordException(it)
        }
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
        FirebaseCrashlytics.getInstance().log("INFO/$tag: $message")
    }
}
