package com.sinya.projects.sportsdiary.widgets.calendarWidget

import com.sinya.projects.sportsdiary.data.database.repository.MorningRepository
import com.sinya.projects.sportsdiary.data.database.repository.TrainingRepository
import com.sinya.projects.sportsdiary.presentation.home.DayOfMonth
import com.sinya.projects.sportsdiary.presentation.home.MonthCalendarResult
import jakarta.inject.Inject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

class GetWidgetDataUseCase @Inject constructor(
    private val morningRepository: MorningRepository,
    private val trainingRepository: TrainingRepository
) {
    private suspend fun getMonthCalendarWithInfo(year: Int, month: Int): MonthCalendarResult {
        val firstOfMonth = LocalDate.now()
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val daysCount = 7
        val endExclusive = start.plusDays(daysCount.toLong())

        val morningList = morningRepository.getList(start.toString(), endExclusive.toString())
        val trainingList = trainingRepository.getList(start.toString(), endExclusive.toString())

        val morningDates = morningList.map { it.date }.toHashSet()
        val trainingDates = trainingList.map { it.date }.toHashSet()
        val result = ArrayList<DayOfMonth>(daysCount)
        var d = start

        var todayIndex = -1
        var todayMorning = false
        var todayTraining = false

        repeat(daysCount) { idx ->
            val iso = d.toString()
            val isToday = d == firstOfMonth
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

    suspend operator fun invoke(): CalendarWidgetUiState {
        val ym = YearMonth.now()

        val result = getMonthCalendarWithInfo(ym.year, ym.monthValue)

        return CalendarWidgetUiState(
            year = ym.year,
            month = ym.monthValue,
            monthDays = result.days,
            trainingState = result.todayTraining,
            morningState = result.todayMorning,
        )
    }
}
