package com.sinya.projects.sportsdiary.presentation.home

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinya.projects.sportsdiary.data.database.entity.DataMorning
import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repoTraining: TrainingRepository,
    private val repoMorning: MorningRepository
) : ViewModel() {

    private val _state = mutableStateOf<HomeScreenUiState>(HomeScreenUiState.Loading)
    val state: State<HomeScreenUiState> = _state

    private var currentMonth: YearMonth = YearMonth.now()

    init {
        loadMonth(currentMonth)
    }

    fun onEvent(event: HomeScreenUiEvent) {
        val currentState = _state.value as? HomeScreenUiState.Success ?: return

        when (event) {
            is HomeScreenUiEvent.OnExtended -> {
                _state.value = currentState.copy(calendarExpanded = event.extended)
            }

            is HomeScreenUiEvent.OnButtonMorningClick -> {
                viewModelScope.launch {
                    repoMorning.insertMorning(
                        DataMorning(
                            id = 0,
                            note = null,
                            date = LocalDate.now().toString(),
                            planId = 0
                        )
                    )
                    updateDate()
                }
            }
            is HomeScreenUiEvent.UpdateTrainingCard -> {
                updateTrainingCard()
            }

            is HomeScreenUiEvent.OnShift -> {
                shiftMonth(event.index)
            }
        }
    }

    private suspend fun updateDate() {
        val currentState = _state.value as? HomeScreenUiState.Success ?: return

        val date = LocalDate.now()
        val list = getMonthCalendarWithInfo(date.year, date.monthValue)

        _state.value = currentState.copy(
            monthDays = list.days,
            morningState = list.todayMorning
        )
    }

    private suspend fun getMonthCalendarWithInfo(year: Int, month: Int): MonthCalendarResult {
        val firstOfMonth = LocalDate.of(year, month, 1)
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        // сетка на 5 недель (35 дней). Нужно 6 строк — поставь 42.
        val daysCount = 35
        val endExclusive = start.plusDays(daysCount.toLong())

        val today = LocalDate.now()

        // Тянем утренние и тренировки за диапазон [start; end)
        // DAO: WHERE date >= :start AND date < :end (формат yyyy-MM-dd)
        val morningList = repoMorning.getList(start.toString(), endExclusive.toString())
        val trainingList = repoTraining.getList(start.toString(), endExclusive.toString()) // если есть аналогичный метод

        val list = repoTraining.trainingList()
        Log.d("Tag", list.toString())


        val morningDates = morningList.map { it.date }.toHashSet()
        val trainingDates = trainingList.map { it.date }.toHashSet()
        val result = ArrayList<DayOfMonth>(daysCount)
        var d = start

        var todayIndex = -1
        var todayMorning = false
        var todayTraining = false

        repeat(daysCount) { idx ->
            val iso = d.toString() // yyyy-MM-dd
            val isToday = d == today
            val isCurrentMonthCell = d.monthValue == month && d.year == year
            val hasMorning = iso in morningDates
            val hasTraining = iso in trainingDates

            if (isToday) {
                todayIndex = idx
                todayMorning = hasMorning
                todayTraining = hasTraining
            }

            result += DayOfMonth(
                currentMonth = isCurrentMonthCell,
                day = d.dayOfMonth,
                month = d.monthValue,
                year = d.year,
                trainingState = hasTraining,
                morningState = hasMorning
            )
            d = d.plusDays(1)
        }

        return MonthCalendarResult(
            days = result,
            todayMorning = todayMorning,
            todayTraining = todayTraining,
            todayIndex = todayIndex
        )
    }

    private fun shiftMonth(delta: Int) {
        val next = currentMonth.plusMonths(delta.toLong())
        if (next == currentMonth) return
        currentMonth = next
        loadMonth(currentMonth)
    }

    private fun updateTrainingCard(date: LocalDate = LocalDate.now()) = viewModelScope.launch {
        val currentState = _state.value as? HomeScreenUiState.Success ?: return@launch

        val trainingCards = repoTraining.getList(start = date.toString(), end = date.plusDays(1).toString())

        _state.value = currentState.copy(
            trainingList = trainingCards,
        )
    }

    private fun loadMonth(ym: YearMonth) = viewModelScope.launch {
        val date = LocalDate.now()

        val trainingCards = repoTraining.getList(start = date.toString(), end = date.plusDays(1).toString())
        val result = getMonthCalendarWithInfo(ym.year, ym.monthValue)

        _state.value = if (_state.value is HomeScreenUiState.Success) {
            val currentState = _state.value as HomeScreenUiState.Success
            currentState.copy(
                year = ym.year,
                month = ym.monthValue,
                trainingList = trainingCards,
                monthDays = result.days,
            )
        } else {
            HomeScreenUiState.Success(
                year = ym.year,
                month = ym.monthValue,
                trainingList = trainingCards,
                monthDays = result.days,
                trainingState = result.todayTraining,
                morningState = result.todayMorning, // true только для «сегодня» в текущем месяце
            )
        }
    }
}


