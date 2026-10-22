package com.sinya.projects.home

import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.datastore.SettingsEngine
import com.sinya.projects.domain.model.DayOfMonth
import com.sinya.projects.domain.model.MorningEntry
import com.sinya.projects.domain.repository.HealthManager
import com.sinya.projects.domain.repository.HealthSdkStatus
import com.sinya.projects.domain.useCase.GetMorningListUseCase
import com.sinya.projects.domain.useCase.GetProportionRangeListUseCase
import com.sinya.projects.domain.useCase.GetTrainingRangeListUseCase
import com.sinya.projects.domain.useCase.MarkDayMorningExerciseUseCase
import com.sinya.projects.domain.useCase.MarkDaysListMorningUseCase
import com.sinya.projects.domain.useCase.UpsertMorningUseCase
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

    private val _selectedDate = MutableStateFlow(LocalDate.now())

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
                cachedKcal = data.totalKcalToday
                cachedSleep = data.sleepDurationMinutes

                updateIfSuccess {
                    it.copy(
                        stepsToday = data.stepsToday,
                        totalKcalToday = data.totalKcalToday,
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
                val day = state.monthDays.find { it.date == state.date.date } ?: return@launch

                insertMorningUseCase(
                    MorningEntry(
                        id = 0,
                        note = day.note,
                        date = _selectedDate.value.toString(),
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
                            if (item.date == it.date.date) item.copy(note = event.value)
                            else item
                        }
                    )
                }
            }

            is HomeEvent.OnCheckClick -> {
                updateIfSuccess {
                    it.copy(
                        monthDays = it.monthDays.map { item ->
                            if (item.date == event.it.date) item.copy(state = !event.it.state)
                            else item
                        }
                    )
                }
            }

            is HomeEvent.OnShift -> {
                val oldDate = _selectedDate.value
                val newDate = oldDate.plusMonths(event.index)
                val newLength = newDate.lengthOfMonth()
                val oldLength = oldDate.lengthOfMonth()
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

                _selectedDate.value = LocalDate.of(newDate.year, newDate.monthValue, newDay)
            }

            is HomeEvent.PickDay -> {
                _selectedDate.value = event.date
            }

            HomeEvent.OnErrorShown -> updateIfSuccess {
                it.copy(errorMessage = null)
            }
        }
    }

    private fun morningExercisesButtonClick(
        morningState: Boolean,
        date: LocalDate,
    ) = viewModelScope.launch {
        markDayMorningExerciseUseCase(
            morningState = morningState,
            item = MorningEntry(
                id = 0,
                note = (state.value as HomeUiState.Success).monthDays.find { it.date == date }?.note,
                date = date.toString(),
                categoryId = (state.value as HomeUiState.Success).currentPlanId
            )
        ).fold(
            onSuccess = {
                updateIfSuccess { state ->
                    val updatedMonthDays = state.monthDays.map { day ->
                        if (day.date == date) day.copy(morningState = morningState)
                        else day
                    }
                    state.copy(monthDays = updatedMonthDays)
                }
            },
            onFailure = { error ->
                error.printStackTrace()
                updateIfSuccess {
                    it.copy(errorMessage = error.toString())
                }
            }
        )
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
                    date = it.date.toString(),
                    categoryId = state.currentPlanId
                )
            }
        ).fold(
            onSuccess = {

            },
            onFailure = {
                updateIfSuccess {
                    it.copy(errorMessage = it.toString())
                }
                Log.d("HomeViewModel", it.toString())
            }
        )
    }

    private fun loadMonthDataFlow(date: LocalDate): Flow<HomeUiState.Success> {
        val firstOfMonth = LocalDate.of(date.year, date.monthValue, 1)
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val lastOfMonth = LocalDate.of(date.year, date.monthValue, date.lengthOfMonth())
        val finish = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        val daysCount = ChronoUnit.DAYS.between(start, finish).toInt() + 1

        return combine(
            settingsEngine.uiState,
            getTrainingListUseCase(start.toString(), finish.toString()),
            getMorningListUseCase(start.toString(), finish.toString()),
            getTrainingListUseCase(date.toString(), date.toString()),
            getProportionListUseCase(date.toString(), date.toString())
        ) { config, trainingListMonth, morningList, trainingListDay, proportionListToday ->

            val morningDates = morningList.map { it.date }.toHashSet()
            val trainingDates = trainingListMonth.map { it.date }.toHashSet()
            val monthDays = ArrayList<DayOfMonth>(daysCount)
            var d = start

            repeat(daysCount) {
                val iso = d.toString()
                val isCurrentMonthCell = d.monthValue == date.monthValue && d.year == date.year

                monthDays += DayOfMonth(
                    date = d,
                    currentMonth = isCurrentMonthCell,
                    trainingState = iso in trainingDates,
                    morningState = iso in morningDates,
                    note = morningList.find { it.date == iso }?.note
                )
                d = d.plusDays(1)
            }

            val currentExpanded = (_state.value as? HomeUiState.Success)?.calendarExpanded ?: false
            val today = monthDays.firstOrNull { it.date == date }

            HomeUiState.Success(
                date = today ?: monthDays.first(),
                currentPlanId = config.planMorningId,
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


