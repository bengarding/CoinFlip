package com.helsinkiwizard.cointoss.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.helsinkiwizard.cointoss.data.room.CoinTossDatabase
import com.helsinkiwizard.core.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    context: Context,
    database: CoinTossDatabase? = null
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
}
