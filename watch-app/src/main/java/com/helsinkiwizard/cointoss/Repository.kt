package com.helsinkiwizard.cointoss

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.helsinkiwizard.core.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class Repository(context: Context) : BaseRepository(context) {

    companion object {
        val TILE_RESOURCE_VERSION = intPreferencesKey("tile_resources_version")
        val CUSTOM_COIN_HEADS = stringPreferencesKey("custom_coin_heads")
        val CUSTOM_COIN_TAILS = stringPreferencesKey("custom_coin_tails")
    }

    val getResourceVersion: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[TILE_RESOURCE_VERSION] ?: 0
        }

    suspend fun setCustomCoin(headsUri: Uri, tailsUri: Uri) {
        savePreference(CUSTOM_COIN_HEADS, headsUri.toString())
        savePreference(CUSTOM_COIN_TAILS, tailsUri.toString())
    }
    val getCustomCoinHeads: Flow<Uri> = context.dataStore.data
        .map { preferences ->
            Uri.parse(preferences[CUSTOM_COIN_HEADS])
        }
    val getCustomCoinTails: Flow<Uri> = context.dataStore.data
        .map { preferences ->
            Uri.parse(preferences[CUSTOM_COIN_TAILS])
        }

    suspend fun setResourceVersion(value: Int) = savePreference(TILE_RESOURCE_VERSION, value)
}
