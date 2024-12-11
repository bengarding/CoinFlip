package com.helsinkiwizard.cointoss.utils

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.CATEGORY_BROWSABLE
import android.net.Uri
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityClient.FILTER_REACHABLE
import com.google.android.gms.wearable.NodeClient
import com.helsinkiwizard.core.CoreConstants.PHONE_NAV_TO_CREATE_COIN_CAPABILITY
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await
import timber.log.Timber

suspend fun isAppInstalledOnPhone(capabilityClient: CapabilityClient): Boolean {
    return try {
        capabilityClient
            .getCapability(PHONE_NAV_TO_CREATE_COIN_CAPABILITY, FILTER_REACHABLE)
            .await()
            .nodes
            .isNotEmpty()
    } catch (e: Exception) {
        Timber.e(e, "Error getting nodes from capability client ")
        false
    }
}

suspend fun isConnectedToAnyNode(nodeClient: NodeClient): Boolean {
    return try {
        nodeClient.connectedNodes.await().isNotEmpty()
    } catch (e: Exception) {
        Timber.e(e, "Error getting nodes from node client ")
        false
    }
}

suspend fun launchDeepLinkOnPhone(
    remoteActivityHelper: RemoteActivityHelper,
    deepLink: String
): Boolean {
    val data = Uri.parse(deepLink)
    val intent = Intent(ACTION_VIEW)
        .addCategory(CATEGORY_BROWSABLE)
        .setData(data)

    try {
        remoteActivityHelper.startRemoteActivity(intent).await()
        return true
    } catch (e: CancellationException) {
        return false
    } catch (e: Exception) {
        Timber.e(e, "Error sending intent via RemoteActivityHelper")
        return false
    }
}
