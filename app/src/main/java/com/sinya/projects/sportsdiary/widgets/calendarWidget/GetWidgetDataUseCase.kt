package com.sinya.projects.sportsdiary.widgets.calendarWidget

import com.sinya.projects.sportsdiary.domain.repository.MorningRepository
import com.sinya.projects.sportsdiary.domain.repository.TrainingRepository
import com.sinya.projects.sportsdiary.domain.model.DayOfMonth
import com.sinya.projects.sportsdiary.domain.model.MonthCalendarResult
import jakarta.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

class GetWidgetDataUseCase @Inject constructor(
    private val morningRepository: MorningRepository,
    private val trainingRepository: TrainingRepository
) {
    private suspend fun getMonthCalendarWithInfo(date: LocalDate): MonthCalendarResult {
        val firstOfMonth = LocalDate.now()
        val start = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        val daysCount = 7
        val endExclusive = start.plusDays(daysCount.toLong())

        return combine(
            morningRepository.getList(start.toString(), endExclusive.toString()),
            trainingRepository.getList(start.toString(), endExclusive.toString())
        ) { morningList, trainingList ->

            val morningDates = morningList.map { it.date }.toHashSet()
            val trainingDates = trainingList.map { it.date }.toHashSet()
            val result = ArrayList<DayOfMonth>(daysCount)
            var d = start

            repeat(daysCount) { idx ->
                val iso = d.toString()
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

            MonthCalendarResult(days = result)
        }.first()
    }

    suspend operator fun invoke(): CalendarWidgetUiState {
        val date = LocalDate.now()
        val result = getMonthCalendarWithInfo(date)

        return CalendarWidgetUiState(
            date = date,
            monthDays = result.days
        )
    }
}
