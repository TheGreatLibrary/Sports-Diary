package com.sinya.projects.health

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.domain.repository.HealthSdkStatus
import com.sinya.projects.domain.utils.UrlConfig
import com.sinya.projects.domain.utils.openUrl
import com.sinya.projects.health.components.*
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.theme.LocalAppGradients
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HealthDataScreen(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: HealthDataViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionContract = remember(viewModel) { viewModel.getPermissionContract() }
    val permissionIntent = remember(viewModel) { viewModel.getPermissionIntent() }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = permissionContract
    ) { granted ->
        if (granted.isNotEmpty()) {
            viewModel.loadHealthData()
        }
    }

    HealthDataView(
        state = state,
        onEvent = { viewModel.loadHealthData() },
        onBackClick = onBackClick,
        permissions = viewModel.permissions,
        getPermissionIntent = permissionIntent,
        permissionsLauncher = permissionsLauncher,
        clickToMarket = { context.openUrl(UrlConfig.healthAPK) }
    )
}

@Composable
private fun HealthDataView(
    state: HealthDashboardState,
    onEvent: () -> Unit,
    onBackClick: () -> Unit,
    clickToMarket: () -> Unit,
    permissionsLauncher: ManagedActivityResultLauncher<Set<String>, Set<String>>,
    permissions: Set<String>,
    getPermissionIntent: Intent,

    ) {
    val gradient = LocalAppGradients.current
    val context = LocalContext.current

    ScreenColumn(
        navigationType = TypeTopNavigation.WithIcon(
            title = when (state.sdkStatus) {
                HealthSdkStatus.AVAILABLE -> if (state.isConnected) stringResource(R.string.connected)
                else stringResource(R.string.disconnected)

                HealthSdkStatus.NOT_INSTALLED -> stringResource(R.string.hc_not_installed)
                HealthSdkStatus.NOT_SUPPORTED -> stringResource(R.string.not_supported)
            },
            onBackClick = onBackClick,
            painter = when (state.sdkStatus) {
                HealthSdkStatus.AVAILABLE, HealthSdkStatus.NOT_INSTALLED -> com.sinya.projects.ui.R.drawable.ic_plus
                else -> com.sinya.projects.ui.R.drawable.cat_change
            },
            onClick = {
                when (state.sdkStatus) {
                    HealthSdkStatus.AVAILABLE -> {
                        try {
                            permissionsLauncher.launch(permissions)
                        } catch (_: Exception) {
                            runCatching { context.startActivity(getPermissionIntent) }
                        }
                    }

                    HealthSdkStatus.NOT_INSTALLED -> {
                        clickToMarket()
                    }

                    else -> {
                        onEvent()
                    }
                }
            }
        ),
    ) {
        if (state.sdkStatus != HealthSdkStatus.AVAILABLE || state.statusMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (state.sdkStatus != HealthSdkStatus.AVAILABLE) {
                        stringResource(R.string.google_health_connect)
                    } else {
                        state.statusMessage ?: ""
                    },
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        ParamHealthCard(
            modifier = Modifier.fillMaxWidth(),
            gradient = gradient.basedCard2Gradient
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.shapes.extraLarge
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.today_b),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 11.sp,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    )
                }

                Text(
                    text = stringResource(R.string.steps_count, state.stepsToday),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TodayMetricBlock(
                        value = "${state.activeKcalToday}",
                        label = stringResource(R.string.active_kcal)
                    )
                    TodayMetricBlock(
                        value = "${state.exerciseMinutesToday}",
                        label = stringResource(R.string.exercise_minutes)
                    )
                    TodayMetricBlock(
                        value = "${state.activeHoursToday}",
                        label = stringResource(R.string.standing_hours)
                    )
                }
                ActivityRingsCanvas(
                    kcalProgress = (state.activeKcalToday / 600f).coerceIn(0.05f, 1f),
                    exerciseProgress = (state.exerciseMinutesToday / 45f).coerceIn(0.05f, 1f),
                    standingProgress = (state.activeHoursToday / 12f).coerceIn(0.05f, 1f),
                    modifier = Modifier
                        .size(165.dp)
                        .padding(8.dp)
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 190.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ParamHealthCard(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                gradient = gradient.basedCard2Gradient,
                verticalArr = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.exercise),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = state.latestExerciseDate,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 11.sp
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ExerciseValueLine(
                        value = String.format(Locale.US, "%.2f", state.distanceKmToday),
                        unit = stringResource(R.string.km)
                    )
                    ExerciseValueLine(
                        value = "${state.exerciseMinutesToday}",
                        unit = stringResource(R.string.minute)
                    )
                    ExerciseValueLine(
                        value = "${state.activeKcalToday}",
                        unit = stringResource(R.string.kcal)
                    )
                }
            }
            BentoSparklineCard(
                title = stringResource(R.string.heart_rate),
                subtitle = if (state.restingHeartRate > 0) stringResource(
                    R.string.rhr,
                    state.restingHeartRate
                ) else stringResource(R.string.today),
                mainValue = if (state.latestHeartRate > 0) stringResource(
                    R.string.bpm,
                    state.latestHeartRate
                ) else stringResource(
                    R.string.bpm,
                    "--"
                ),
                hourlyValues = state.heartRateHourly,
                barColor = MaterialTheme.colorScheme.secondary,
                gradient = gradient.basedCard2Gradient,
                mutedColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BentoSparklineCard(
                title = stringResource(R.string.blood_oxygen),
                subtitle = stringResource(R.string.spo2),
                mainValue = if (state.latestSpo2 > 0) "${state.latestSpo2.roundToInt()}%" else "--%",
                hourlyValues = state.spo2Hourly,
                barColor = MaterialTheme.colorScheme.secondary,
                mutedColor = MaterialTheme.colorScheme.onSecondary,
                gradient = gradient.basedCard2Gradient,
                modifier = Modifier
                    .weight(1f)
                    .height(195.dp)
            )
            BentoSparklineCard(
                title = stringResource(R.string.stress_hrv),
                subtitle = if (state.hrvRmssd > 0) stringResource(
                    R.string.hrv_ms,
                    state.hrvRmssd.roundToInt()
                ) else stringResource(R.string.recovery),
                mainValue = if (state.stressScore > 0) "${state.stressScore}" else "--",
                hourlyValues = state.stressHourly,
                barColor = MaterialTheme.colorScheme.secondary,
                gradient = gradient.basedCard2Gradient,
                mutedColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .weight(1f)
                    .height(195.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ParamHealthCard(
                modifier = Modifier.weight(1f),
                gradient = gradient.basedCard2Gradient
            ) {
                Text(
                    text = stringResource(R.string.sleep_vitals),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = stringResource(
                        R.string.h_m,
                        state.sleepDurationMinutes / 60,
                        state.sleepDurationMinutes % 60
                    ),
                    fontSize = 22.sp,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                MiniStatRow(
                    stringResource(R.string.vo2_max),
                    if (state.vo2Max > 0) "%.1f".format(state.vo2Max) else "--"
                )
                MiniStatRow(
                    stringResource(R.string.breath),
                    if (state.respiratoryRate > 0) stringResource(
                        R.string.min,
                        state.respiratoryRate.roundToInt()
                    ) else "--"
                )
                MiniStatRow(
                    stringResource(R.string.steps),
                    stringResource(R.string.st, state.floorsClimbedToday.roundToInt())
                )
                MiniStatRow(
                    stringResource(R.string.water),
                    stringResource(R.string.liters, "%.1f".format(state.hydrationLitersToday))
                )
            }

            ParamHealthCard(
                modifier = Modifier.weight(1f),
                gradient = gradient.basedCard2Gradient
            ) {
                Text(
                    text = stringResource(R.string.body_scale),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = state.weightKg?.let { "${"%.1f".format(it)} kg" } ?: "-- kg",
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                MiniStatRow(
                    stringResource(R.string.fat),
                    state.bodyFatPercent?.let { "${"%.1f".format(it)}%" } ?: "--")
                MiniStatRow(
                    stringResource(R.string.muscle),
                    state.leanBodyMassKg?.let { stringResource(R.string.kg_h, "%.2f".format(it)) }
                        ?: "--")
                MiniStatRow(
                    stringResource(R.string.bones),
                    state.boneMassKg?.let { stringResource(R.string.kg_h, "%.2f".format(it)) }
                        ?: "--")
                MiniStatRow(
                    stringResource(R.string.bmr),
                    state.bmrKcal?.let { stringResource(R.string.kkal, it.roundToInt()) } ?: "--"
                )
            }
        }

        Spacer(Modifier.height(100.dp))
    }
}


