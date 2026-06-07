package com.sinya.projects.sportsdiary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.core.data.dataBase.entity.DataMorning
import com.sinya.projects.sportsdiary.core.domain.model.DayOfMonth
import com.sinya.projects.sportsdiary.core.domain.useCase.GetMorningListUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.GetTrainingRangeListUseCase
import com.sinya.projects.sportsdiary.core.domain.useCase.MarkDayMorningExerciseUseCase
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
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
class HomeViewModel @Inject constructor(
    private val settingsEngine: SettingsEngine,
    private val markDayMorningExerciseUseCase: MarkDayMorningExerciseUseCase,
    private val getMorningListUseCase: GetMorningListUseCase,
    private val getTrainingListUseCase: GetTrainingRangeListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    init {
        viewModelScope.launch {
            _selectedDate
                .flatMapLatest { date -> loadMonthDataFlow(date) }
                .collect { newState ->
                    _state.value = newState
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
                event.date,
                event.planId
            )

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
        planId: Int?
    ) = viewModelScope.launch {
        markDayMorningExerciseUseCase(
            morningState = morningState,
            item = DataMorning(
                id = 0,
                note = null,
                date = date.toString(),
                planId = planId
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
                updateIfSuccess {
                    it.copy(errorMessage = error.toString())
                }
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
            getTrainingListUseCase(date.toString(), date.plusDays(1).toString())
        ) { config, trainingListMonth, morningList, trainingListDay ->

            val morningDates = morningList.map { it.date }.toHashSet()
            val trainingDates = trainingListMonth.map { it.date }.toHashSet()
            val monthDays = ArrayList<DayOfMonth>(daysCount)
            var d = start

            repeat(daysCount) {
                val iso = d.toString()
                val isCurrentMonthCell = d.monthValue == date.monthValue && d.year == date.year

                monthDays += DayOfMonth(
                    currentMonth = isCurrentMonthCell,
                    date = d,
                    trainingState = iso in trainingDates,
                    morningState = iso in morningDates
                )
                d = d.plusDays(1)
            }

            val currentExpanded = (_state.value as? HomeUiState.Success)?.calendarExpanded ?: false

            HomeUiState.Success(
                date = date,
                currentPlanId = config.planMorningId,
                monthDays = monthDays,
                trainingList = trainingListDay,
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


