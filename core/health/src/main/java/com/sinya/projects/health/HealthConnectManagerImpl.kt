package com.sinya.projects.health

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.sinya.projects.domain.model.HealthData
import com.sinya.projects.domain.managers.HealthManager
import com.sinya.projects.domain.managers.HealthSdkStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.reflect.KClass

internal class HealthConnectManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
//    private val client: HealthConnectClient
) : HealthManager {

    private val client by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    private val fetchMutex = Mutex()
    private var cachedData: HealthData? = null
    private var lastFetchTimeMs: Long = 0L
    private val cacheCooldownMs = 60_000L

    override val permissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(FloorsClimbedRecord::class),
        HealthPermission.getReadPermission(ElevationGainedRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(RestingHeartRateRecord::class),
        HealthPermission.getReadPermission(HeartRateVariabilityRmssdRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(RespiratoryRateRecord::class),
        HealthPermission.getReadPermission(Vo2MaxRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(BodyFatRecord::class),
        HealthPermission.getReadPermission(LeanBodyMassRecord::class),
        HealthPermission.getReadPermission(BoneMassRecord::class),
        HealthPermission.getReadPermission(BodyWaterMassRecord::class),
        HealthPermission.getReadPermission(BasalMetabolicRateRecord::class)
    )

    override suspend fun getSdkStatus(): HealthSdkStatus {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthSdkStatus.AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE -> HealthSdkStatus.NOT_SUPPORTED
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthSdkStatus.NOT_INSTALLED
            else -> HealthSdkStatus.NOT_SUPPORTED
        }
    }

    override fun getPermissionIntent(): Intent {
        return Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS")
    }

    override fun getPermissionContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    override suspend fun fetchHealthData(): HealthData = fetchMutex.withLock {
        val nowMs = System.currentTimeMillis()
        cachedData?.let { if (nowMs - lastFetchTimeMs < cacheCooldownMs) return it }

        val granted = runCatching { client.permissionController.getGrantedPermissions() }.getOrDefault(emptySet())
        if (granted.isEmpty()) return HealthData()

        val zoneId = ZoneId.systemDefault()
        val now = Instant.now()
        val startOfToday = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant()

        val todayFilter = TimeRangeFilter.between(startOfToday, now)
        val weekFilter = TimeRangeFilter.between(now.minus(7, ChronoUnit.DAYS), now)
        val monthFilter = TimeRangeFilter.between(now.minus(30, ChronoUnit.DAYS), now)


        val todayAggregates = fetchTodayAggregatesBatch(todayFilter, granted)

        val activeHours = safeWithPermission(StepsRecord::class, 0, granted) {
            client.aggregateGroupByDuration(
                AggregateGroupByDurationRequest(setOf(StepsRecord.COUNT_TOTAL), todayFilter, Duration.ofHours(1))
            ).count { (it.result[StepsRecord.COUNT_TOTAL] ?: 0L) >= 50L }
        }

        val exerciseData = fetchExerciseData(weekFilter, startOfToday, zoneId, granted)
        val heartRateData = fetchHeartRateData(todayFilter, weekFilter, zoneId, granted)
        val oxygenData = fetchOxygenData(todayFilter, weekFilter, zoneId, granted)
        val stressData = fetchStressData(weekFilter, heartRateData.latestHeartRate, heartRateData.heartRateHourly, granted)
        val otherVitals = fetchOtherVitals(weekFilter, monthFilter, granted)
        val bodyComposition = fetchBodyComposition(monthFilter, granted)

        val result = HealthData(
            stepsToday = todayAggregates.steps,
            activeKcalToday = if (todayAggregates.activeKcal > 0) todayAggregates.activeKcal else (todayAggregates.totalKcal / 3),
            totalKcalToday = todayAggregates.totalKcal,
            exerciseMinutesToday = exerciseData.todayMinutes,
            activeHoursToday = activeHours,
            latestExerciseDate = exerciseData.latestDate,
            distanceKmToday = todayAggregates.distanceKm,
            floorsClimbedToday = todayAggregates.floors,
            elevationGainMeters = todayAggregates.elevation,
            latestHeartRate = heartRateData.latestHeartRate,
            restingHeartRate = heartRateData.restingHeartRate,
            heartRateHourly = heartRateData.heartRateHourly,
            latestSpo2 = oxygenData.latestSpo2,
            spo2Hourly = oxygenData.spo2Hourly,
            hrvRmssd = stressData.hrvRmssd,
            stressScore = stressData.stressScore,
            stressHourly = stressData.stressHourly,
            sleepDurationMinutes = otherVitals.sleepMins,
            respiratoryRate = otherVitals.respRate,
            vo2Max = otherVitals.vo2Max,
            hydrationLitersToday = todayAggregates.hydrationLiters,
            weightKg = bodyComposition.weightKg,
            bodyFatPercent = bodyComposition.bodyFat,
            leanBodyMassKg = bodyComposition.leanMass,
            boneMassKg = bodyComposition.boneMass,
            bodyWaterKg = bodyComposition.waterMass,
            bmrKcal = bodyComposition.bmr
        )

        cachedData = result
        lastFetchTimeMs = System.currentTimeMillis()
        return result
    }

    private suspend fun fetchTodayAggregatesBatch(
        filter: TimeRangeFilter,
        granted: Set<String>
    ): TodayBatchAggregateResult {
        val metrics = mutableSetOf<AggregateMetric<*>>()

        fun addIfGranted(cls: KClass<out Record>, metric: AggregateMetric<*>) {
            if (HealthPermission.getReadPermission(cls) in granted) metrics.add(metric)
        }

        addIfGranted(StepsRecord::class, StepsRecord.COUNT_TOTAL)
        addIfGranted(ActiveCaloriesBurnedRecord::class, ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL)
        addIfGranted(TotalCaloriesBurnedRecord::class, TotalCaloriesBurnedRecord.ENERGY_TOTAL)
        addIfGranted(DistanceRecord::class, DistanceRecord.DISTANCE_TOTAL)
        addIfGranted(FloorsClimbedRecord::class, FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL)
        addIfGranted(ElevationGainedRecord::class, ElevationGainedRecord.ELEVATION_GAINED_TOTAL)
        addIfGranted(HydrationRecord::class, HydrationRecord.VOLUME_TOTAL)

        if (metrics.isEmpty()) return TodayBatchAggregateResult()

        return try {
            val res = client.aggregate(AggregateRequest(metrics, filter))
            TodayBatchAggregateResult(
                steps = res[StepsRecord.COUNT_TOTAL] ?: 0L,
                activeKcal = res[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories?.roundToInt() ?: 0,
                totalKcal = res[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories?.roundToInt() ?: 0,
                distanceKm = (res[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0) / 1000.0,
                floors = res[FloorsClimbedRecord.FLOORS_CLIMBED_TOTAL] ?: 0.0,
                elevation = res[ElevationGainedRecord.ELEVATION_GAINED_TOTAL]?.inMeters ?: 0.0,
                hydrationLiters = res[HydrationRecord.VOLUME_TOTAL]?.inLiters ?: 0.0
            )
        } catch (e: Exception) {
            TodayBatchAggregateResult()
        }
    }

    private suspend fun fetchExerciseData(
        filter: TimeRangeFilter,
        startOfToday: Instant,
        zoneId: ZoneId,
        granted: Set<String>
    ): ExerciseResult {
        val sessions = safeWithPermission(ExerciseSessionRecord::class, emptyList(), granted) {
            client.readRecords(ReadRecordsRequest(ExerciseSessionRecord::class, filter, ascendingOrder = false, pageSize = 20)).records
        }
        val todayMins = sessions.filter { it.startTime.isAfter(startOfToday) }
            .sumOf { ChronoUnit.MINUTES.between(it.startTime, it.endTime) }.toInt()
        val latest = sessions.firstOrNull()
        val dateStr = latest?.endTime?.atZone(zoneId)?.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)) ?: "Today"
        return ExerciseResult(todayMins, dateStr)
    }

    private suspend fun fetchHeartRateData(
        todayFilter: TimeRangeFilter,
        weekFilter: TimeRangeFilter,
        zoneId: ZoneId,
        granted: Set<String>
    ): HeartRateResult {
        val records = safeWithPermission(HeartRateRecord::class, emptyList(), granted) {
            client.readRecords(ReadRecordsRequest(HeartRateRecord::class, todayFilter)).records
        }
        val allSamples = records.flatMap { it.samples }
        val latest = allSamples.maxByOrNull { it.time }?.beatsPerMinute ?: 0L
        val hourly = MutableList(24) { 0f }
        allSamples.groupBy { it.time.atZone(zoneId).hour }.forEach { (hour, samples) ->
            if (hour in 0..23 && samples.isNotEmpty()) hourly[hour] = samples.map { it.beatsPerMinute }.average().toFloat()
        }
        val resting = readLatestRecord(RestingHeartRateRecord::class, weekFilter, granted)?.beatsPerMinute ?: 0L
        return HeartRateResult(latest, resting, hourly)
    }

    private suspend fun fetchOxygenData(
        todayFilter: TimeRangeFilter,
        weekFilter: TimeRangeFilter,
        zoneId: ZoneId,
        granted: Set<String>
    ): OxygenResult {
        val records = safeWithPermission(OxygenSaturationRecord::class, emptyList(), granted) {
            client.readRecords(ReadRecordsRequest(OxygenSaturationRecord::class, todayFilter)).records
        }
        val hourly = MutableList(24) { 0f }
        records.groupBy { it.time.atZone(zoneId).hour }.forEach { (hour, recs) ->
            if (hour in 0..23 && recs.isNotEmpty()) hourly[hour] = recs.map { it.percentage.value }.average().toFloat()
        }
        val latest = records.maxByOrNull { it.time }?.percentage?.value
            ?: readLatestRecord(OxygenSaturationRecord::class, weekFilter, granted)?.percentage?.value
            ?: 0.0
        return OxygenResult(latest, hourly)
    }

    private suspend fun fetchStressData(
        weekFilter: TimeRangeFilter,
        latestHr: Long,
        hrHourly: List<Float>,
        granted: Set<String>
    ): StressResult {
        val latestHrv = readLatestRecord(HeartRateVariabilityRmssdRecord::class, weekFilter, granted)?.heartRateVariabilityMillis ?: 0.0
        val stressScore = when {
            latestHrv > 0 -> (100 - (latestHrv * 1.1).roundToInt()).coerceIn(15, 95)
            latestHr > 0 -> ((latestHr - 50) * 1.2).roundToInt().coerceIn(15, 95)
            else -> 0
        }
        val stressHourly = hrHourly.map { hr -> if (hr > 0f) ((hr - 45f) * 1.3f).coerceIn(10f, 100f) else 0f }
        return StressResult(latestHrv, stressScore, stressHourly)
    }

    private suspend fun fetchOtherVitals(
        weekFilter: TimeRangeFilter,
        monthFilter: TimeRangeFilter,
        granted: Set<String>
    ): OtherVitalsResult {
        val sleep = readLatestRecord(SleepSessionRecord::class, weekFilter, granted)?.let {
            ChronoUnit.MINUTES.between(it.startTime, it.endTime)
        } ?: 0L
        val resp = readLatestRecord(RespiratoryRateRecord::class, weekFilter, granted)?.rate ?: 0.0
        val vo2 = readLatestRecord(Vo2MaxRecord::class, monthFilter, granted)?.vo2MillilitersPerMinuteKilogram ?: 0.0
        return OtherVitalsResult(sleep, resp, vo2)
    }

    private suspend fun fetchBodyComposition(
        filter: TimeRangeFilter,
        granted: Set<String>
    ): BodyCompositionResult {
        return BodyCompositionResult(
            weightKg = readLatestRecord(WeightRecord::class, filter, granted)?.weight?.inKilograms,
            bodyFat = readLatestRecord(BodyFatRecord::class, filter, granted)?.percentage?.value,
            leanMass = readLatestRecord(LeanBodyMassRecord::class, filter, granted)?.mass?.inKilograms,
            boneMass = readLatestRecord(BoneMassRecord::class, filter, granted)?.mass?.inKilograms,
            waterMass = readLatestRecord(BodyWaterMassRecord::class, filter, granted)?.mass?.inKilograms,
            bmr = readLatestRecord(BasalMetabolicRateRecord::class, filter, granted)?.basalMetabolicRate?.inKilocaloriesPerDay
        )
    }

    // Берет только 1 самую свежую запись из БД вместо выгрузки массива за 30 дней
    private suspend fun <T : Record> readLatestRecord(
        recordType: KClass<T>,
        filter: TimeRangeFilter,
        granted: Set<String>
    ): T? = safeWithPermission(recordType, null, granted) {
        client.readRecords(
            ReadRecordsRequest(
                recordType = recordType,
                timeRangeFilter = filter,
                ascendingOrder = false,
                pageSize = 1
            )
        ).records.firstOrNull()
    }

    private suspend fun <T : Record, R> safeWithPermission(
        recordType: KClass<T>,
        default: R,
        granted: Set<String>,
        block: suspend () -> R
    ): R {
        val perm = HealthPermission.getReadPermission(recordType)
        return if (perm in granted) {
            try {
                block()
            } catch (e: Exception) {
                default
            }
        } else {
            default
        }
    }

    private data class TodayBatchAggregateResult(
        val steps: Long = 0L,
        val activeKcal: Int = 0,
        val totalKcal: Int = 0,
        val distanceKm: Double = 0.0,
        val floors: Double = 0.0,
        val elevation: Double = 0.0,
        val hydrationLiters: Double = 0.0
    )
    private data class ExerciseResult(val todayMinutes: Int, val latestDate: String)
    private data class HeartRateResult(val latestHeartRate: Long, val restingHeartRate: Long, val heartRateHourly: List<Float>)
    private data class OxygenResult(val latestSpo2: Double, val spo2Hourly: List<Float>)
    private data class StressResult(val hrvRmssd: Double, val stressScore: Int, val stressHourly: List<Float>)
    private data class OtherVitalsResult(val sleepMins: Long, val respRate: Double, val vo2Max: Double)
    private data class BodyCompositionResult(val weightKg: Double?, val bodyFat: Double?, val leanMass: Double?, val boneMass: Double?, val waterMass: Double?, val bmr: Double?)
}