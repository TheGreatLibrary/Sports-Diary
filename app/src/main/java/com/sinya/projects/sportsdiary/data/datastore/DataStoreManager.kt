package com.sinya.projects.sportsdiary.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val THEME_MODE = booleanPreferencesKey("theme_mode")
    private val LANG_MODE = stringPreferencesKey("lang_mode")
    private val PLAN_MORNING_ID = intPreferencesKey("plan_morning_id")

    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    private fun <T> read(key: Preferences.Key<T>, defaultValue: T): Flow<T> = context.dataStore.data
        .map { settings ->
            settings[key] ?: defaultValue
        }

    suspend fun setThemeMode(value: Boolean) = save(THEME_MODE, value)
    fun getThemeMode(): Flow<Boolean> = read(THEME_MODE, false)

    suspend fun setLangMode(value: String) = save(LANG_MODE, value)
    fun getLangMode(): Flow<String> = read(LANG_MODE, "ru")

    suspend fun setPlanMorningId(value: Int) = save(PLAN_MORNING_ID, value)
    fun getPlanMorningId(): Flow<Int> = read(PLAN_MORNING_ID, 0)
}