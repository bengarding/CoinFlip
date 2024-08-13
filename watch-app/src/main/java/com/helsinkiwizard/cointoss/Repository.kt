package com.helsinkiwizard.cointoss

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.helsinkiwizard.core.BaseRepository
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class Repository(context: Context) : BaseRepository(context) {

    companion object {
        val TILE_RESOURCE_VERSION = intPreferencesKey("tile_resources_version")
        val CUSTOM_COIN_HEADS = stringPreferencesKey("custom_coin_heads")
        val CUSTOM_COIN_TAILS = stringPreferencesKey("custom_coin_tails")
        val CUSTOM_COIN_NAME = stringPreferencesKey("custom_coin_name")
        val SHOW_CUSTOM_COIN_DIALOG = booleanPreferencesKey("show_custom_coin_dialog")
    }

    val getResourceVersion: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[TILE_RESOURCE_VERSION] ?: 0
        }

    suspend fun setCustomCoin(headsUri: Uri, tailsUri: Uri, name: String) {
        savePreference(CUSTOM_COIN_HEADS, headsUri.toString())
        savePreference(CUSTOM_COIN_TAILS, tailsUri.toString())
        savePreference(CUSTOM_COIN_NAME, name)
    }

    val getCustomCoin: Flow<CustomCoinUiModel?> = context.dataStore.data
        .map { preferences ->
            val headsUri = preferences[CUSTOM_COIN_HEADS]
            val tailsUri = preferences[CUSTOM_COIN_TAILS]

            if (headsUri == null || tailsUri == null) {
                null
            } else {
                CustomCoinUiModel(
                    headsUri = Uri.parse(headsUri),
                    tailsUri = Uri.parse(tailsUri),
                    name = preferences[CUSTOM_COIN_NAME] ?: EMPTY_STRING
                )
            }
        }

    suspend fun disableShowSendToWatchDialog() = savePreference(SHOW_CUSTOM_COIN_DIALOG, false)
    val getShowSendToWatchDialog: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_CUSTOM_COIN_DIALOG] ?: true
        }

    suspend fun setResourceVersion(value: Int) = savePreference(TILE_RESOURCE_VERSION, value)
}
