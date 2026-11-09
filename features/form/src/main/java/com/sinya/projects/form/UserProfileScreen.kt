package com.sinya.projects.form

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.domain.enums.Gender
import com.sinya.projects.domain.model.ProportionRow
import com.sinya.projects.domain.model.RadioItem
import com.sinya.projects.domain.model.TypeTopNavigation
import com.sinya.projects.domain.utils.parseDateToStringUI
import com.sinya.projects.form.components.ActiveSheet
import com.sinya.projects.form.components.HeartRateSheetContent
import com.sinya.projects.form.components.SleepPickerSheetContent
import com.sinya.projects.form.components.SliderSheetContent
import com.sinya.projects.form.components.UserProfilePlaceholder
import com.sinya.projects.form.components.WeightPickerSheetContent
import com.sinya.projects.ui.components.ChartSortedRow
import com.sinya.projects.ui.components.DateTimePickerModal
import com.sinya.projects.ui.components.GoalCardItem
import com.sinya.projects.ui.components.screen.ScreenColumn
import com.sinya.projects.ui.components.sheet.CustomModalSheet
import java.time.LocalDate

@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit,
) {
    val viewModel: UserProfileViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        UserProfileUiState.Loading -> UserProfilePlaceholder(
            onBackClick = onBackClick,
            title = stringResource(R.string.profile_title)
        )

        is UserProfileUiState.Success -> {
            UserProfileView(
                onBackClick = onBackClick,
                state = state as UserProfileUiState.Success,
                onEvent = viewModel::onEvent
            )
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
private fun UserProfileView(
    onBackClick: () -> Unit,
    state: UserProfileUiState.Success,
    onEvent: (UserProfileEvent) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var activeSheet by remember { mutableStateOf(ActiveSheet.NONE) }

    val calculatedAge = remember(state.birthDateMillis) {
        val today = LocalDate.now().year
        val birth = state.birthDateMillis.year
        (today - birth).coerceIn(0, 147)
    }
    val effectiveMaxHr = if (state.autoCalculateMaxHr) (220 - calculatedAge) else state.maxHeartRate

    ScreenColumn(
        navigationType = TypeTopNavigation.WithoutIcon(
            onBackClick = onBackClick,
            title = stringResource(R.string.profile_title)
        ),
        arrangement = Arrangement.spacedBy(12.dp)
    ) {
        ChartSortedRow(
            radioOptions = Gender.entries.map { gender ->
                RadioItem(
                    text = stringResource(gender.title),
                    icon = null,
                    value = gender
                )
            },
            selectedOption = state.gender,
            onOptionSelected = { gender -> onEvent(UserProfileEvent.OnGenderSelected(gender)) }
        )

        GoalCardItem(
            item = ProportionRow(
                id = 0,
                title = stringResource(R.string.years_date),
                value = parseDateToStringUI(state.birthDateMillis),
                unitMeasure = pluralStringResource(
                    R.plurals.calculated_years,
                    calculatedAge,
                    calculatedAge,
                    calculatedAge,
                    calculatedAge
                )
            ),
            onClick = { showDatePicker = true }
        )

        GoalCardItem(
            item = ProportionRow(
                id = 0,
                title = stringResource(R.string.max_plc),
                value = "$effectiveMaxHr" + if (state.autoCalculateMaxHr) stringResource(R.string.auto_data) else "",
                unitMeasure = stringResource(R.string.plc_min)
            ),
            onClick = { activeSheet = ActiveSheet.HEART_RATE }
        )

        GoalCardItem(
            item = ProportionRow(
                id = 0,
                title = stringResource(R.string.weight_chci),
                value = state.targetWeightKg.toString(),
                unitMeasure = stringResource(com.sinya.projects.ui.R.string.kg)
            ),
            onClick = { activeSheet = ActiveSheet.WEIGHT }
        )

        GoalCardItem(
            item = ProportionRow(
                id = 0,
                title = stringResource(R.string.steps_chci),
                value = state.dailyStepsGoal.toString(),
                unitMeasure = stringResource(R.string.steps_unit)
            ),
            onClick = { activeSheet = ActiveSheet.STEPS }
        )

        GoalCardItem(
            item = ProportionRow(
                id = 0,
                title = stringResource(R.string.lenght_sleep),
                value = stringResource(
                    R.string.time_sleep,
                    String.format("%02d", state.sleepDurationMinutes / 60),
                    String.format("%02d", state.sleepDurationMinutes % 60)
                ),
                unitMeasure = stringResource(R.string.count_sleep)
            ),
            onClick = { activeSheet = ActiveSheet.SLEEP }
        )

        GoalCardItem(
            item = ProportionRow(
                id = 0,
                title = stringResource(R.string.chci_kkal),
                value = state.dailyCaloriesGoal.toString(),
                unitMeasure = stringResource(R.string.kkal_unit)
            ),
            onClick = { activeSheet = ActiveSheet.CALORIES },
        )

        if (showDatePicker) {
            DateTimePickerModal(
                onDateTimeSelected = { date ->
                    onEvent(UserProfileEvent.OnDateSelected(date))
                    showDatePicker = false
                },
                withTime = false,
                onDismiss = { showDatePicker = false }
            )
        }

        if (activeSheet != ActiveSheet.NONE) {
            CustomModalSheet(
                onDismiss = { activeSheet = ActiveSheet.NONE }
            ) {
                when (activeSheet) {
                    ActiveSheet.WEIGHT -> WeightPickerSheetContent(
                        initialWeight = state.targetWeightKg,
                        onConfirm = {
                            onEvent(UserProfileEvent.OnWeightSelected(it))
                            activeSheet = ActiveSheet.NONE
                        }
                    )

                    ActiveSheet.SLEEP -> SleepPickerSheetContent(
                        initialMinutes = state.sleepDurationMinutes,
                        onConfirm = {
                            onEvent(UserProfileEvent.OnSleepSelected(it))
                            activeSheet = ActiveSheet.NONE
                        }
                    )

                    ActiveSheet.STEPS -> SliderSheetContent(
                        title = stringResource(R.string.steps_chci),
                        unit = stringResource(R.string.steps_unit),
                        initialValue = state.dailyStepsGoal,
                        valueRange = 1000f..100000f,
                        stepSize = 500,
                        onConfirm = {
                            onEvent(UserProfileEvent.OnStepsSelected(it))
                            activeSheet = ActiveSheet.NONE
                        }
                    )

                    ActiveSheet.CALORIES -> SliderSheetContent(
                        title = stringResource(R.string.chci_kkal),
                        unit = stringResource(R.string.kkal_unit),
                        initialValue = state.dailyCaloriesGoal,
                        valueRange = 50f..2000f,
                        stepSize = 50,
                        onConfirm = {
                            onEvent(UserProfileEvent.OnCaloriesSelected(it))
                            activeSheet = ActiveSheet.NONE
                        }
                    )

                    ActiveSheet.HEART_RATE -> HeartRateSheetContent(
                        autoCalculate = state.autoCalculateMaxHr,
                        calculatedHr = 220 - calculatedAge,
                        manualHr = state.maxHeartRate,
                        onConfirm = { auto, manual ->
                            onEvent(
                                UserProfileEvent.OnHeartRateSelected(auto, manual)
                            )
                            activeSheet = ActiveSheet.NONE
                        }
                    )

                    else -> {}
                }
            }
        }
    }
}

