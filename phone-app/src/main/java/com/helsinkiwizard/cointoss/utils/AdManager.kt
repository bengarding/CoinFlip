package com.helsinkiwizard.cointoss.utils

import android.app.Activity
import android.os.Bundle
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import timber.log.Timber

object AdManager {
    private var isPersonalizedAds: Boolean = true

    fun updateConsentStatus(activity: Activity) {
        val consentInfo = UserMessagingPlatform.getConsentInformation(activity)

        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInfo.requestConsentInfoUpdate(
            activity,
            params,
            {
                isPersonalizedAds = consentInfo.consentStatus != ConsentInformation.ConsentStatus.REQUIRED
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    formError?.let {
                        Timber.e("Error loading consent form: ${it.message}")
                    }
                }
            },
            { requestError ->
                Timber.e("Error requesting consent: ${requestError.message}")
            }
        )
    }

    fun getAdRequest(): AdRequest {
        val extras = Bundle().apply {
            if (!isPersonalizedAds) {
                putString("npa", "1")
            }
        }
        return AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()
    }
}
