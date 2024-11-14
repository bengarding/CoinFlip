package com.helsinkiwizard.cointoss.utils

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

fun Activity.launchInAppReview(
    onComplete: (() -> Unit)? = null,
) {
    val reviewManager = ReviewManagerFactory.create(this)
    val request = reviewManager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val flow = reviewManager.launchReviewFlow(this, reviewInfo)
            flow.addOnCompleteListener {
                onComplete?.invoke()
            }
        } else {
            onComplete?.invoke()
        }
    }
}
