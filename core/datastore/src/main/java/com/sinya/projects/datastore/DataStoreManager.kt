package com.sinya.projects.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sinya.projects.domain.enums.TypeLanguages
import com.sinya.projects.domain.model.UiConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val SHOW_TRAINING_WARNING = booleanPreferencesKey("show_training_warning")
    private val THEME_MODE = booleanPreferencesKey("theme_mode")
    private val MAP_7_MODE = booleanPreferencesKey("map_7_mode")
    private val LANG_MODE = stringPreferencesKey("lang_mode")
    private val CATEGORY_MORNING_ID = intPreferencesKey("category_morning_id")

    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    suspend fun readAllCriticalPreferences(): UiConfig {
        val prefs = context.dataStore.data.first()
        return UiConfig(
            dark = prefs[THEME_MODE] ?: false,
            map7 = prefs[MAP_7_MODE] ?: true,
            language = prefs[LANG_MODE] ?: TypeLanguages.RU.code,
            planMorningId = prefs[CATEGORY_MORNING_ID],
            trainingWarning = prefs[SHOW_TRAINING_WARNING] ?: false,
        )
    }

    suspend fun setThemeMode(value: Boolean) = save(THEME_MODE, value)
    suspend fun setMap7Mode(value: Boolean) = save(MAP_7_MODE, value)

    suspend fun setShowTrainingWarningState(value: Boolean) = save(SHOW_TRAINING_WARNING, value)

    suspend fun setLangMode(value: String) = save(LANG_MODE, value)

    suspend fun setPlanMorningId(value: Int?) {
        context.dataStore.edit { settings ->
            if (value != null) {
                settings[CATEGORY_MORNING_ID] = value
            } else {
                settings.remove(CATEGORY_MORNING_ID)
            }
        }
    }
    fun getPlanMorningId(): Flow<Int?> = context.dataStore.data
        .map { settings ->
            if (settings.contains(CATEGORY_MORNING_ID)) {
                settings[CATEGORY_MORNING_ID]
            } else {
                null
            }
        }
}