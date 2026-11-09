package com.sinya.projects.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.domain.managers.HealthManager
import com.sinya.projects.domain.managers.SettingsEngine
import com.sinya.projects.domain.model.DayOfMonth
import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.useCase.GetMorningListUseCase
import com.sinya.projects.domain.useCase.GetProportionRangeListUseCase
import com.sinya.projects.domain.useCase.GetTrainingRangeListUseCase
import com.sinya.projects.domain.useCase.MarkDayMorningExerciseUseCase
import com.sinya.projects.domain.useCase.MarkDaysListMorningUseCase
import com.sinya.projects.domain.useCase.UpsertMorningUseCase
import com.sinya.projects.domain.utils.parseDateToStringDB
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine,
    private val markDayMorningExerciseUseCase: MarkDayMorningExerciseUseCase,
    private val markDaysListMorningUseCase: MarkDaysListMorningUseCase,
    private val getMorningListUseCase: GetMorningListUseCase,
    private val insertMorningUseCase: UpsertMorningUseCase,
    private val getTrainingListUseCase: GetTrainingRangeListUseCase,
    private val getProportionListUseCase: GetProportionRangeListUseCase,
    private val healthManager: HealthManager
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    private var cachedSteps: Long = 0L
    private var cachedKcal: Int = 0
    private var cachedSleep: Long = 0L

    private val _selectedDate = MutableStateFlow(LocalDateTime.now())

    init {
        loadHealthData()

        viewModelScope.launch {
            _selectedDate
                .flatMapLatest { date -> loadMonthDataFlow(date) }
                .collect { newState ->
                    _state.update { prev ->
                        if (prev is HomeUiState.Success) {
                            newState.copy(
                                stepsToday = prev.stepsToday,
                                totalKcalToday = prev.totalKcalToday,
                                sleepDurationMinutes = prev.sleepDurationMinutes
                            )
                        } else {
                            newState
                        }
                    }
                }
        }
    }


    fun loadHealthData() {
        viewModelScope.launch {
            try {
                val data = healthManager.fetchHealthData()
                cachedSteps = data.stepsToday
                cachedKcal = data.activeKcalToday
                cachedSleep = data.sleepDurationMinutes

                updateIfSuccess {
                    it.copy(
                        stepsToday = data.stepsToday,
                        totalKcalToday = data.activeKcalToday,
                        sleepDurationMinutes = data.sleepDurationMinutes,
                    )
                }
            } catch (e: Exception) {
                updateIfSuccess { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnExtended -> updateIfSuccess {
                it.copy(calendarExpanded = event.extended)
            }

            is HomeEvent.OnButtonMorningClick -> morningExercisesButtonClick(
                event.morningState,
                event.date
            )

            is HomeEvent.ToggleListOfMorningDays -> toggleListOfMorningDays()

            is HomeEvent.SaveNote -> viewModelScope.launch {
                val state = _state.value as? HomeUiState.Success ?: return@launch
                val day =
                    state.monthDays.find { it.date.toLocalDate() == state.date.date.toLocalDate() }
                        ?: return@launch

                insertMorningUseCase(
                    MorningEntry(
                        id = 0,
                        note = day.note,
                        date = _selectedDate.value.toLocalDate().toString(),
                        categoryId = state.currentPlanId
                    )
                ).fold(
                    onSuccess = {},
                    onFailure = {
                        updateIfSuccess {
                            it.copy(errorMessage = it.toString())
                        }
                        Log.d("HomeViewModel", it.toString())
                    }
                )
            }

            is HomeEvent.OnValueChange -> {
                updateIfSuccess {
                    it.copy(
                        monthDays = it.monthDays.map { item ->
                            if (item.date.toLocalDate() == it.date.date.toLocalDate()) item.copy(
                                note = event.value
                            )
                            else item
                        }
                    )
                }
            }

            is HomeEvent.OnCheckClick -> {
                updateIfSuccess {
                    it.copy(
                        monthDays = it.monthDays.map { item ->
                            if (item.date.toLocalDate() == event.it.date.toLocalDate()) item.copy(
                                state = !event.it.state
                            )
                            else item
                        }
                    )
                }
            }

            is HomeEvent.OnShift -> {
                val oldDate = _selectedDate.value
                val newDate = oldDate.plusMonths(event.index)
                val newLength = newDate.toLocalDate().lengthOfMonth()
                val oldLength = oldDate.toLocalDate().lengthOfMonth()
                val oldDay = oldDate.dayOfMonth

                val newDay = when {
                    event.index > 0 -> {
                        val offsetFromStart = oldDay.coerceAtMost(7)
                        offsetFromStart.coerceAtMost(newLength)
                    }

                    else -> {
                        val offsetFromEnd = (oldLength - oldDay + 1).coerceAtMost(7)
                        (newLength - offsetFromEnd + 1).coerceIn(1, newLength)
                    }
                }
                val date = LocalDateTime.now()
                _selectedDate.value = LocalDate.of(newDate.year, newDate.monthValue, newDay)
                    .atTime(date.hour, date.minute)
            }

            is HomeEvent.PickDay -> {
                val date = LocalDateTime.now()
                _selectedDate.value = event.date.toLocalDate().atTime(date.hour, date.minute)
            }

            HomeEvent.OnErrorShown -> updateIfSuccess {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun morningExercisesButtonClick(
        morningState: Boolean,
        date: LocalDateTime,
    ) = viewModelScope.launch {
        val currentState = state.value as? HomeUiState.Success ?: return@launch
        val targetDay = currentState.monthDays.find { it.date.toLocalDate() == date.toLocalDate() }

        markDayMorningExerciseUseCase(
            morningState = morningState,
            item = MorningEntry(
                id = 0,
                note = targetDay?.note,
                date = date.toLocalDate().toString(),
                categoryId = currentState.currentPlanId
            )
        ).onFailure { error ->
            error.printStackTrace()
            updateIfSuccess {
                it.copy(errorMessage = error.toString())
            }
        }
    }

    private fun toggleListOfMorningDays() = viewModelScope.launch {
        val state = _state.value as? HomeUiState.Success ?: return@launch
        if (state.monthDays.none { it.state }) return@launch

        markDaysListMorningUseCase(
            marked = !state.monthDays.first { it.state }.morningState,
            items = state.monthDays.filter { it.state }.map {
                MorningEntry(
                    id = 0,
                    note = it.note,
                    date = it.date.toLocalDate().toString(),
                    categoryId = state.currentPlanId
                )
            }
        ).fold(
            onSuccess = {
                Log.d("HomeViewModel", it.toString())
            },
            onFailure = {
                updateIfSuccess {
                    it.copy(errorMessage = it.toString())
                }
                Log.d("HomeViewModel", it.toString())
            }
        )
    }

    private fun loadMonthDataFlow(date: LocalDateTime): Flow<HomeUiState.Success> {
        val selectedDay = date.toLocalDate()

        val firstOfMonth = LocalDate.of(date.year, date.monthValue, 1)
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay()
        val lastOfMonth = LocalDate.of(date.year, date.monthValue, selectedDay.lengthOfMonth())
        val finish = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(23, 59, 59)

        val dayStart = parseDateToStringDB(selectedDay.atStartOfDay())
        val dayFinish = parseDateToStringDB(selectedDay.atTime(23, 59, 59))

        val daysCount = ChronoUnit.DAYS.between(start, finish).toInt() + 1

        return combine(
            settingsEngine.uiState,
            getTrainingListUseCase(parseDateToStringDB(start), parseDateToStringDB(finish)),
            getMorningListUseCase(parseDateToStringDB(start), parseDateToStringDB(finish)),
            getTrainingListUseCase(dayStart, dayFinish),
            getProportionListUseCase(dayStart, dayFinish)
        ) { config, trainingListMonth, morningList, trainingListDay, proportionListToday ->

            val morningDates = morningList.map { it.date.take(10) }.toHashSet()
            val trainingDates = trainingListMonth.map { it.date.take(10) }.toHashSet()
            val morningNotesByDay = morningList.associateBy({ it.date.take(10) }, { it.note })

            val monthDays = ArrayList<DayOfMonth>(daysCount)
            var d = start

            repeat(daysCount) {
                val dayKey = parseDateToStringDB(d).take(10)
                val isCurrentMonthCell = d.monthValue == date.monthValue && d.year == date.year

                monthDays += DayOfMonth(
                    date = d,
                    currentMonth = isCurrentMonthCell,
                    trainingState = dayKey in trainingDates,
                    morningState = dayKey in morningDates,
                    note = morningNotesByDay[dayKey]
                )
                d = d.plusDays(1)
            }

            val currentExpanded = (_state.value as? HomeUiState.Success)?.calendarExpanded ?: false
            val today = monthDays.firstOrNull { it.date.toLocalDate() == selectedDay }
                ?: monthDays.firstOrNull { it.currentMonth }
                ?: monthDays.first()

            HomeUiState.Success(
                date = today.copy(date = date.toLocalDate().atTime(date.hour, date.minute)),
                currentPlanId = config.planMorningId,
                targetStepsCount = config.dailyStepsGoal,
                targetSleep = config.sleepDurationMinutes,
                targetKcalActiveCount = config.targetActiveKcal,
                monthDays = monthDays,
                trainingList = trainingListDay,
                proportionList = proportionListToday,
                calendarExpanded = currentExpanded,
                errorMessage = null
            )
        }
    }

    private fun updateIfSuccess(transform: (HomeUiState.Success) -> HomeUiState.Success) {
        _state.update { currentState ->
            if (currentState is HomeUiState.Success) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}


