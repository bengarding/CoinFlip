package com.helsinkiwizard.cointoss.tile

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ModifiersBuilders
import com.helsinkiwizard.cointoss.Constants.EXTRA_START_FLIPPING
import com.helsinkiwizard.cointoss.ui.MainActivity
import com.helsinkiwizard.core.CoreConstants.PACKAGE_NAME

/**
 * Creates a Clickable that can be used to launch an activity.
 */
internal fun launchActivityClickable(
    clickableId: String,
    androidActivity: ActionBuilders.AndroidActivity
) = ModifiersBuilders.Clickable.Builder()
    .setId(clickableId)
    .setOnClick(
        ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(androidActivity)
            .build()
    )
    .build()

internal fun openCoin() = ActionBuilders.AndroidActivity.Builder()
    .setMessagingActivity()
    .addKeyToExtraMapping(
        EXTRA_START_FLIPPING,
        ActionBuilders.booleanExtra(true)
    )
    .build()

private fun ActionBuilders.AndroidActivity.Builder.setMessagingActivity(): ActionBuilders.AndroidActivity.Builder {
    return setPackageName(PACKAGE_NAME)
        .setClassName(PACKAGE_NAME.plus(".ui.").plus(MainActivity.TAG))
}
