package com.sinya.projects.health

import com.sinya.projects.domain.managers.HealthSdkStatus

data class HealthDashboardState(
    val isConnected: Boolean = false,
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val sdkStatus: HealthSdkStatus = HealthSdkStatus.AVAILABLE,


    // Верхняя карточка TODAY
    val stepsToday: Long = 0L,
    val activeKcalToday: Int = 0,
    val totalKcalToday: Int = 0,
    val exerciseMinutesToday: Int = 0,
    val activeHoursToday: Int = 0, // Часы, в которые было пройдено > 100 шагов (аналог Standing)

    // Карточка EXERCISE (оранжевая)
    val latestExerciseTitle: String = "Tренировка",
    val latestExerciseDate: String = "Сегодня",
    val distanceKmToday: Double = 0.0,
    val floorsClimbedToday: Double = 0.0,
    val elevationGainMeters: Double = 0.0,

    // Карточки с почасовыми графиками (00:00 - 24:00)
    val latestHeartRate: Long = 0L,
    val restingHeartRate: Long = 0L,
    val heartRateHourly: List<Float> = List(24) { 0f },

    val latestSpo2: Double = 0.0,
    val spo2Hourly: List<Float> = List(24) { 0f },

    // Стресс / Вариабельность пульса (HRV RMSSD)
    val hrvRmssd: Double = 0.0,
    val stressScore: Int = 0, // Расчетный индекс 1..100 на основе HRV и пульса
    val stressHourly: List<Float> = List(24) { 0f },

    // Сон и восстановление
    val sleepDurationMinutes: Long = 0L,
    val respiratoryRate: Double = 0.0,
    val vo2Max: Double = 0.0,
    val skinTempDelta: Double = 0.0,
    val hydrationLitersToday: Double = 0.0,

    // Состав тела (Умные весы)
    val weightKg: Double? = null,
    val bodyFatPercent: Double? = null,
    val leanBodyMassKg: Double? = null,
    val boneMassKg: Double? = null,
    val bodyWaterKg: Double? = null,
    val bmrKcal: Double? = null
)