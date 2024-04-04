package com.helsinkiwizard.cointoss.data

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.helsinkiwizard.cointoss.data.room.CoinTossDatabase
import com.helsinkiwizard.cointoss.data.room.CustomCoin
import com.helsinkiwizard.cointoss.ui.model.CustomCoinUiModel
import com.helsinkiwizard.core.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    context: Context,
    private val database: CoinTossDatabase? = null
) : BaseRepository(context) {

    companion object {
        private val THEME_MODE = stringPreferencesKey("selected_theme")
        private val MATERIAL_YOU = booleanPreferencesKey("material_you")
    }

    suspend fun setTheme(themeMode: ThemeMode) = savePreference(THEME_MODE, themeMode.name)
    val getThemeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            ThemeMode.valueOf(preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name)
        }

    suspend fun setMaterialYou(materialYou: Boolean) = savePreference(MATERIAL_YOU, materialYou)
    val getMaterialYou: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[MATERIAL_YOU] ?: true
        }

    suspend fun storeCustomCoin(headsUri: Uri, tailsUri: Uri, name: String) {
        database?.let {
            val customCoin = CustomCoin(
                heads = headsUri.toString(),
                tails = tailsUri.toString(),
                name = name,
                selected = true
            )
            it.customCoinDao().deselectThenInsert(customCoin)
        }
    }

    fun getSelectedCustomCoin(): Flow<CustomCoinUiModel?> {
        return database?.customCoinDao()?.getSelectedCoin()?.map { it?.toUiModel() } ?: flowOf()
    }

    fun getCustomCoins(): Flow<List<CustomCoinUiModel>> {
        return database?.customCoinDao()?.getAllFlow()?.map { list ->
            list.map { it.toUiModel() }
        } ?: flowOf()
    }
}