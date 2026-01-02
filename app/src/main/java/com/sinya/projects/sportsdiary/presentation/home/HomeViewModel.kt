package com.sinya.projects.sportsdiary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import com.sinya.projects.sportsdiary.domain.useCase.GetMorningListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetTrainingListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.MarkDayMorningExerciseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val markDayMorningExerciseUseCase: MarkDayMorningExerciseUseCase,
    private val getMorningListUseCase: GetMorningListUseCase,
    private val getTrainingListUseCase: GetTrainingListUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnExtended -> updateIfSuccess { it.copy(calendarExpanded = event.extended) }

            is HomeEvent.OnButtonMorningClick -> morningExercisesButtonClick(
                event.morningState,
                event.date,
                event.planId
            )

            is HomeEvent.OnShift -> shiftMonth(event.index)

            is HomeEvent.PickDay -> viewModelScope.launch {
                val currentState = state.value as? HomeUiState.Success ?: return@launch
                if (event.date.monthValue != currentState.date.monthValue) {
                    val index = (event.date.monthValue - currentState.date.monthValue).toLong()
                    updateIfSuccess { it.copy(date = event.date.plusMonths(-index)) }
                    shiftMonth(index)
                } else {
                    updateIfSuccess { it.copy(date = event.date) }
                }
                loadTrainingsForDate(event.date)
            }

            HomeEvent.OnErrorShown -> updateIfSuccess { it.copy(errorMessage = null) }
        }
    }

    private fun morningExercisesButtonClick(morningState: Boolean, date: LocalDate, planId: Int) {
        viewModelScope.launch {
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
    }

    fun loadMonth(date: LocalDate = LocalDate.now()) = viewModelScope.launch {
        loadMonthCalendar(date)
        loadTrainingsForDate(date)
    }

    private suspend fun loadTrainingsForDate(date: LocalDate) {
        getTrainingListUseCase(
            start = date.toString(),
            end = date.plusDays(1).toString()
        ).fold(
            onSuccess = { trainings ->
                updateIfSuccess { it.copy(trainingList = trainings) }
            },
            onFailure = { error ->
                updateIfSuccess {
                    it.copy(errorMessage = error.localizedMessage ?: "Failed to load trainings")
                }
            }
        )
    }

    private suspend fun loadMonthCalendar(date: LocalDate) {
        val firstOfMonth = LocalDate.of(date.year, date.monthValue, 1)
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val daysCount = 35
        val endExclusive = start.plusDays(daysCount.toLong())

        val morningList = getMorningListUseCase(
            start.toString(),
            endExclusive.toString()
        ).getOrElse { emptyList() }
        val trainingList = getTrainingListUseCase(
            start.toString(),
            endExclusive.toString()
        ).getOrElse { emptyList() }

        val morningDates = morningList.map { it.date }.toHashSet()
        val trainingDates = trainingList.map { it.date }.toHashSet()

        val result = ArrayList<DayOfMonth>(daysCount)
        var d = start

        repeat(daysCount) { _ ->
            val iso = d.toString() // yyyy-MM-dd
            val isCurrentMonthCell = d.monthValue == date.monthValue && d.year == date.year
            val hasMorning = iso in morningDates
            val hasTraining = iso in trainingDates


            result += DayOfMonth(
                currentMonth = isCurrentMonthCell,
                date = d,
                trainingState = hasTraining,
                morningState = hasMorning
            )
            d = d.plusDays(1)
        }

        if (_state.value is HomeUiState.Success) {
            updateIfSuccess {
                it.copy(
                    date = date,
                    monthDays = result
                )
            }
        } else {
            _state.value = HomeUiState.Success(
                date = date,
                trainingList = emptyList(),
                monthDays = result,
                calendarExpanded = false,
                errorMessage = null
            )
        }
    }

    private fun shiftMonth(delta: Long) {
        val currentState = _state.value as? HomeUiState.Success ?: return

        val next = currentState.date.plusMonths(delta)
        if (next == currentState.date) return

        loadMonth(next)
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


