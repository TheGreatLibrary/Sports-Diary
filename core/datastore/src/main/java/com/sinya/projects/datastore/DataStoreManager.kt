package com.sinya.projects.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sinya.projects.domain.enums.Gender
import com.sinya.projects.domain.enums.TypeLanguages
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
internal class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // CRITICAL DATA
    private val SHOW_TRAINING_WARNING = booleanPreferencesKey("show_training_warning")
    private val THEME_MODE = booleanPreferencesKey("theme_mode")
    private val MAP_7_MODE = booleanPreferencesKey("map_7_mode")
    private val LANG_MODE = stringPreferencesKey("lang_mode")
    private val CATEGORY_MORNING_ID = intPreferencesKey("category_morning_id")

    // OPTIONAL DATA
    private val GENDER = intPreferencesKey("gender")
    private val BIRTH_DATE_MILLIS = longPreferencesKey("birth_date_millis")
    private val TARGET_WEIGHT_KG = floatPreferencesKey("target_weight_kg")
    private val TARGET_STEPS_COUNT = intPreferencesKey("target_steps_count")
    private val TARGET_SLEEP_COUNT = intPreferencesKey("target_sleep_count")
    private val TARGET_KCAL_COUNT = intPreferencesKey("target_kcal_count")
    private val AUTO_CALCULATE_MAX_HR = booleanPreferencesKey("auto_calculate_max_hr")
    private val MAX_HEART_RATE = intPreferencesKey("max_heart_rate")


    private suspend fun <T> save(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    suspend fun readAllCriticalPreferences(): CriticalPrefs {
        val prefs = context.dataStore.data.first()
        return CriticalPrefs(
            dark = prefs[THEME_MODE] ?: false,
            map7 = prefs[MAP_7_MODE] ?: true,
            language = prefs[LANG_MODE] ?: TypeLanguages.RU.code,
            planMorningId = prefs[CATEGORY_MORNING_ID],
            trainingWarning = prefs[SHOW_TRAINING_WARNING] ?: false,
        )
    }

    suspend fun readAllOptionalPreferences(): OptionalPrefs {
        val prefs = context.dataStore.data.first()
        return OptionalPrefs(
            gender = Gender.getGenderByValue(prefs[GENDER] ?: 0),
            birthDateMillis = prefs[BIRTH_DATE_MILLIS]?.let { millis ->
                Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
//                LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
            } ?: LocalDate.of(2008, 1, 1),
            targetWeight = prefs[TARGET_WEIGHT_KG] ?: 75f,
            targetStepAtDay = prefs[TARGET_STEPS_COUNT] ?: 10000,
            targetSleepAtDay = prefs[TARGET_SLEEP_COUNT] ?: 480,
            targetCaloriesAtDay = prefs[TARGET_KCAL_COUNT] ?: 400,
            autoCalculateMaxHr = prefs[AUTO_CALCULATE_MAX_HR] ?: true,
            maxHeartRate = prefs[MAX_HEART_RATE] ?: 100
        )
    }

    // CRITICAL METHODS
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

    // OPTIONAL METHODS
    suspend fun setGender(value: Int) = save(GENDER, value)
    suspend fun setBirthDateMillis(value: Long) = save(BIRTH_DATE_MILLIS, value)
    suspend fun setTargetWeight(value: Float) = save(TARGET_WEIGHT_KG, value)
    suspend fun setTargetStepsCount(value: Int) = save(TARGET_STEPS_COUNT, value)
    suspend fun setTargetSleepCount(value: Int) = save(TARGET_SLEEP_COUNT, value)
    suspend fun setTargetKcalCount(value: Int) = save(TARGET_KCAL_COUNT, value)
    suspend fun setAutoCalculateMaxHr(value: Boolean) = save(AUTO_CALCULATE_MAX_HR, value)
    suspend fun setMaxHeartRate(value: Int) = save(MAX_HEART_RATE, value)

}