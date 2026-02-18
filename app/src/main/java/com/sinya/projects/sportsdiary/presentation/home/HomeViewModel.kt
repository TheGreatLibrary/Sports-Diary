package com.sinya.projects.sportsdiary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.datastore.DataStoreManager
import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import com.sinya.projects.sportsdiary.domain.useCase.GetMorningListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.GetTrainingRangeListUseCase
import com.sinya.projects.sportsdiary.domain.useCase.MarkDayMorningExerciseUseCase
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
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
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
                _selectedDate.value = _selectedDate.value.plusMonths(event.index)
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
        val daysCount = 35
        val endExclusive = start.plusDays(daysCount.toLong())

        return combine(
            dataStoreManager.getPlanMorningId(),
            getTrainingListUseCase(start.toString(), endExclusive.toString()),
            getMorningListUseCase(start.toString(), endExclusive.toString()),
            getTrainingListUseCase(date.toString(), date.plusDays(1).toString())
        ) { planId, trainingListMonth, morningList, trainingListDay ->

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
                currentPlanId = planId,
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


