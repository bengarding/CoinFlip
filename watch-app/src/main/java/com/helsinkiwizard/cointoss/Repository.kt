package com.helsinkiwizard.cointoss

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.helsinkiwizard.core.BaseRepository
import com.helsinkiwizard.core.CoreConstants.EMPTY_STRING
import com.helsinkiwizard.core.ui.model.CustomCoinUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class Repository(context: Context) : BaseRepository(context) {

    companion object {
        val TILE_RESOURCE_VERSION = intPreferencesKey("tile_resources_version")
        val CUSTOM_COIN_ID = intPreferencesKey("custom_coin_id")
        val CUSTOM_COIN_NAME = stringPreferencesKey("custom_coin_name")

        private const val FILE_PROVIDER_PREFIX = "content://com.helsinkiwizard.cointoss.file-provider/pictures/"
        private val headsUri = Uri.parse(FILE_PROVIDER_PREFIX + "0.webp")
        private val tailsUri = Uri.parse(FILE_PROVIDER_PREFIX + "1.webp")
    }

    val getResourceVersion: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[TILE_RESOURCE_VERSION] ?: 0
        }

    suspend fun setCustomCoinName(name: String) {
        savePreference(CUSTOM_COIN_NAME, name)
        savePreference(CUSTOM_COIN_ID, Random.nextInt())
    }

    val getCustomCoin: Flow<CustomCoinUiModel> = context.dataStore.data
        .map { preferences ->
            CustomCoinUiModel(
                id = preferences[CUSTOM_COIN_ID] ?: Random.nextInt(),
                headsUri = headsUri,
                tailsUri = tailsUri,
                name = preferences[CUSTOM_COIN_NAME] ?: EMPTY_STRING
            )
        }

    suspend fun setResourceVersion(value: Int) = savePreference(TILE_RESOURCE_VERSION, value)
}
